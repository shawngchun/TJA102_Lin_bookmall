package com.bookmall.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookmall.dto.CartItemDto;
import com.bookmall.dto.CheckoutRequest;
import com.bookmall.entity.BkmlUser;
import com.bookmall.entity.Book;
import com.bookmall.entity.Order;
import com.bookmall.entity.OrderItem;
import com.bookmall.repository.BkmlUserRepository;
import com.bookmall.repository.BookRepository;
import com.bookmall.repository.OrderRepository;
import com.bookmall.service.CartService;
import com.bookmall.service.OrderService;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired private CartService cartService;
    @Autowired private OrderRepository orderRepository;
    @Autowired private BkmlUserRepository userRepository;
    @Autowired private BookRepository bookRepository;

    @Override
    @Transactional // 事務現在在這裡，保護整個業務流程
    public Order checkout(String username, CheckoutRequest request) {
        // 1. 取得用戶
        BkmlUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用戶不存在"));

        // 2. 取得並檢查購物車
        List<CartItemDto> cartItems = cartService.getCartDetails(username);
        if (cartItems.isEmpty()) {
            throw new RuntimeException("購物車是空的");
        }

        // 3. 檢查並扣除庫存 (原本 Controller 的 loop)
        for (CartItemDto item : cartItems) {
            Book book = bookRepository.findById(item.getBookId())
                    .orElseThrow(() -> new RuntimeException("找不到書籍 ID: " + item.getBookId()));
            if (book.getStock() < item.getQuantity()) {
                throw new RuntimeException("書籍 [" + book.getTitle() + "] 庫存不足");
            }
            book.setStock(book.getStock() - item.getQuantity());
            bookRepository.save(book);
        }

        // 4. 建立訂單與明細 (原本 Controller 的 Lambda 轉換)
        Order order = new Order();
        order.setUserId(user.getId());
        order.setReceiverName(request.getReceiverName());
        order.setReceiverAddress(request.getReceiverAddress());
        order.setStatus(0);

        BigDecimal totalAmount = cartItems.stream()
                .map(CartItemDto::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalAmount(totalAmount);

        List<OrderItem> orderItems = cartItems.stream().map(item -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setBookId(item.getBookId());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setCurrentPrice(item.getPrice());
            return orderItem;
        }).collect(Collectors.toList());

        order.setItems(orderItems);

        // 5. 儲存並清空
        Order savedOrder = orderRepository.save(order);
        cartService.clearCart(username);
        
        return savedOrder;
    }

    @Override
    @Transactional
    public void payOrder(Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("訂單不存在"));
        if (order.getStatus() != 0) {
            throw new RuntimeException("訂單狀態不符，無法付款");
        }
        order.setStatus(1);
        orderRepository.save(order);
    }

    @Override
    public List<Order> getUserOrders(String username) {
        BkmlUser user = userRepository.findByUsername(username).orElseThrow();
        return orderRepository.findByUserId(user.getId());
    }
}