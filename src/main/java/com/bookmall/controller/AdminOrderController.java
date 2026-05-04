package com.bookmall.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bookmall.entity.Order;
import com.bookmall.service.AdminOrderService;

@RestController
@RequestMapping("/api/admin/orders")
@PreAuthorize("hasRole('ADMIN')") // 再次強調：確保 @EnableMethodSecurity 有開啟
public class AdminOrderController {

    @Autowired
    private AdminOrderService adminOrderService;
    
    @GetMapping("/search")
    public ResponseEntity<Page<Order>> searchOrders(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
            @RequestParam(required = false) Integer status,
            Pageable pageable) {
        
        return ResponseEntity.ok(adminOrderService.getOrdersByCondition(start, end, status, pageable));
    }
    
    // 1. 取得所有人的訂單列表
    @GetMapping
    public ResponseEntity<Page<Order>> listAllOrders(
            @RequestParam(required = false) Integer status, // 非必填
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        
        return ResponseEntity.ok(adminOrderService.getAllOrders(status, pageable));
    }

    // 2. 查詢特定訂單詳情
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderDetail(@PathVariable Integer id) {
        return ResponseEntity.ok(adminOrderService.getOrderById(id));
    }

    // 3. 修改訂單狀態 (PATCH)
    // 範例 Body: {"status": 3}
    @PatchMapping("/{id}/status")
    public ResponseEntity<Order> changeStatus(
            @PathVariable Integer id, 
            @RequestBody Map<String, Integer> payload) {
        Integer status = payload.get("status");
        return ResponseEntity.ok(adminOrderService.updateOrderStatus(id, status));
    }

    // 4. 依用戶 ID 查詢訂單
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Order>> listUserOrders(@PathVariable Integer userId) {
        return ResponseEntity.ok(adminOrderService.getOrdersByUserId(userId));
    }
}