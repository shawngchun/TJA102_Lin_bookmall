package com.bookmall.controller;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

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
import com.bookmall.dto.OrderResponseDto;
import com.bookmall.entity.Order;
import com.bookmall.service.OrderService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService; // 只注入 Service
    
    @PostMapping("/checkout")
    public ResponseEntity<Order> checkout(
            Principal principal, 
            @RequestBody CheckoutRequest request) {
        
        // principal.getName() 會根據你的 OAuth2 配置回傳 Email
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
        // 從 principal 取得 email (即 UserDetails 中的 username)，但它不是UserDetails，比較像是Authentication
        String email = principal.getName();
        
        // 直接呼叫 Service 層取得結果
        List<Order> orders = orderService.getOrdersByEmail(email);
        
        return ResponseEntity.ok(orders);
    }
    
    @GetMapping("/myneworder")
    public ResponseEntity<OrderResponseDto> getMyNewOrders(java.security.Principal principal) {
    	String email = principal.getName();
    	
    	// 直接呼叫 Service 層取得結果
    	OrderResponseDto dto = orderService.getLatestOrdersByEmail(email);
    	
    	return ResponseEntity.ok(dto);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDto> getOrderById(@PathVariable Integer id, java.security.Principal principal) {
        // 從安全上下文取得 email
        String email = principal.getName();
        
        // 呼叫 Service
        OrderResponseDto orderDto = orderService.findOrderByIdAndUserId(id, email);
        
        return ResponseEntity.ok(orderDto);
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
    
//    @PostMapping("/success")
//    public void handlePaymentSuccess(HttpServletResponse response) throws IOException {
//        // 強制瀏覽器重導向到靜態頁面
//        response.sendRedirect("/paysuccess.html");
//    }
    
    @PostMapping("/success")
    public void handlePaymentSuccess(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 1. 從綠界傳回的參數中取得訂單編號 (MerchantTradeNo)
        // 綠界會將其原本傳過去的編號原封不動傳回
        String merchantTradeNo = request.getParameter("MerchantTradeNo");

        String orderId = extractOrderId(merchantTradeNo);
        // 2. 為了讓前端能精確查詢，我們將這個編號夾帶在 URL 中
        // 假設你的靜態頁面路徑是 /paysuccess.html
        String redirectUrl = "/paysuccess.html?orderId=" + orderId;

        // 3. 執行重導向
        response.sendRedirect(redirectUrl);
    }
    
    private String extractOrderId(String tradeNo) {
        // 解析 "BKML15T..." 拿到 15
        return tradeNo.substring(4, tradeNo.indexOf("T"));
    }
}