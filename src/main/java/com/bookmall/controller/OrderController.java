package com.bookmall.controller;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookmall.dto.CheckoutRequest;
import com.bookmall.entity.BkmlUser;
import com.bookmall.entity.Order;
import com.bookmall.repository.BkmlUserRepository;
import com.bookmall.repository.OrderRepository;
import com.bookmall.service.OrderService;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService; // 只注入 Service
    
    @Autowired
    private BkmlUserRepository bkmlUserRepository; // 只注入 Service
    
    @Autowired
    private OrderRepository orderRepository; // 只注入 Service
    
    @PostMapping("/checkout")
    public ResponseEntity<Order> checkout(
            Principal principal, 
            @RequestBody CheckoutRequest request) {
        
        // principal.getName() 會根據你的 OAuth2 配置回傳 Email 或 Username
        Order order = orderService.checkout(principal.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

//    @PostMapping("/checkout")
//    public ResponseEntity<?> checkout(
//            @AuthenticationPrincipal UserDetails userDetails,
//            @RequestBody CheckoutRequest request) {
//        try {
//            Order order = orderService.checkout(userDetails.getUsername(), request);
//            return ResponseEntity.ok(Map.of(
//                "message", "結帳成功",
//                "orderId", order.getId(),
//                "total", order.getTotalAmount()
//            ));
//        } catch (RuntimeException e) {
//            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
//        }
//    }
    
    @GetMapping("/my")
    public ResponseEntity<List<Order>> getMyOrders(java.security.Principal principal) {
        // 1. principal.getName() 在 OAuth2 會回傳 email，在傳統登入也會回傳 username
        String username = principal.getName();
        System.out.println(username);

        // 2. 透過 username 找到資料庫裡的 user
        BkmlUser user = bkmlUserRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用戶不存在"));

        // 3. 查詢該使用者的所有訂單
        List<Order> orders = orderRepository.findByUserId(user.getId());

        return ResponseEntity.ok(orders);
    }

//    @GetMapping("/my")
//    public ResponseEntity<List<Order>> getMyOrders(@AuthenticationPrincipal UserDetails userDetails) {
//        return ResponseEntity.ok(orderService.getUserOrders(userDetails.getUsername()));
//    }

//    @PostMapping("/{orderId}/pay")
//    public ResponseEntity<?> payOrder(@PathVariable Integer orderId) {
//        try {
//            orderService.payOrder(orderId);
//            return ResponseEntity.ok(Map.of("message", "付款成功"));
//        } catch (RuntimeException e) {
//            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
//        }
//    }
    
    @GetMapping(value = "/{orderId}/pay", produces = "text/html")
    public String payOrder(@PathVariable Integer orderId) {
        // 這裡改為呼叫產生金流表單的邏輯
        return orderService.generatePaymentForm(orderId);
    }
}