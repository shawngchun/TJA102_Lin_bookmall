package com.bookmall.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookmall.dto.OrderItemDTO;
import com.bookmall.entity.OrderItem;
import com.bookmall.service.OrderItemService;

@RestController
@RequestMapping("/api/admin/order-items")
public class AdminOrderItemController {

    @Autowired
    private OrderItemService orderItemService;

    // 1. 列出所有訂單明細 (供管理員大範圍查核)
    @GetMapping
    public ResponseEntity<List<OrderItem>> listAll() {
        return ResponseEntity.ok(orderItemService.getAllOrderItems());
    }

    // 2. 依照訂單 ID 查詢明細
    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<OrderItemDTO>> listByOrder(@PathVariable Integer orderId) {
        return ResponseEntity.ok(orderItemService.getItemsByOrderId(orderId));
    }

    // 3. 修改特定明細
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody OrderItem item) {
        OrderItem updated = orderItemService.updateOrderItem(id, item);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    // 4. 刪除特定明細
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        orderItemService.deleteOrderItem(id);
        return ResponseEntity.ok(Map.of("message", "明細已刪除"));
    }
}