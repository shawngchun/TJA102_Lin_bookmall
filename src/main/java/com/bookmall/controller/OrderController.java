package com.bookmall.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookmall.dto.CartItemDto;
import com.bookmall.dto.CheckoutRequest;
import com.bookmall.entity.BkmlUser;
import com.bookmall.entity.Order;
import com.bookmall.entity.OrderItem;
import com.bookmall.repository.BkmlUserRepository;
import com.bookmall.repository.OrderRepository;
import com.bookmall.service.CartService;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private CartService cartService;
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private BkmlUserRepository userRepository;

    @PostMapping("/checkout")
    @Transactional // 確保訂單存入與 Redis 清空是原子操作
    public ResponseEntity<?> checkout(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody CheckoutRequest request) { // 接收 JSON 收件資訊

        // 1. 透過 username 找到資料庫裡的 user 物件以取得 Long 型別的 userId
        BkmlUser user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("用戶不存在"));

        // 2. 獲取購物車詳情
        List<CartItemDto> cartItems = cartService.getCartDetails(user.getUsername());
        if (cartItems.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "購物車是空的"));
        }

        // 3. 建立訂單主表物件
        Order order = new Order();
        order.setUserId(user.getId()); // SQL 的 user_id
        order.setReceiverName(request.getReceiverName());
        order.setReceiverAddress(request.getReceiverAddress());
        order.setStatus(0); // 預設 0:待付款

        // 4. 計算總額 (Lambda 複習)
        BigDecimal totalAmount = cartItems.stream()
                .map(CartItemDto::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalAmount(totalAmount);

        // 5. 轉換明細 (OrderItem)
        List<OrderItem> orderItems = cartItems.stream().map(item -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order); // 設定關聯
            orderItem.setBookId(item.getBookId());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setCurrentPrice(item.getPrice()); // 紀錄當時價格
            return orderItem;
        }).collect(Collectors.toList());

        order.setItems(orderItems);

        // 6. 存入資料庫並清空 Redis
        orderRepository.save(order);
        cartService.clearCart(user.getUsername());

        return ResponseEntity.ok(Map.of(
            "message", "結帳成功",
            "orderId", order.getId(),
            "total", totalAmount
        ));
    }
    
    @GetMapping("/my")
    public ResponseEntity<List<Order>> getMyOrders(@AuthenticationPrincipal UserDetails userDetails) {
        // 1. 取得當前登入者資訊
        BkmlUser user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("用戶不存在"));

        // 2. 根據 userId 查詢該使用者的所有訂單
        // JPA 會自動根據我們在 Entity 設定的 @OneToMany 把明細也抓回來
        List<Order> orders = orderRepository.findByUserId(user.getId());

        return ResponseEntity.ok(orders);
    }
}