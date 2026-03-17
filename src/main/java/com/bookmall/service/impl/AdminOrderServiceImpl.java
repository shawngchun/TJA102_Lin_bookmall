package com.bookmall.service.impl;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookmall.entity.Order;
import com.bookmall.repository.OrderRepository;
import com.bookmall.service.AdminOrderService;

@Service
public class AdminOrderServiceImpl implements AdminOrderService {

    @Autowired
    private OrderRepository orderRepository;
    
    @Override
    public Page<Order> getOrdersByCondition(LocalDateTime start, LocalDateTime end, Integer status, Pageable pageable) {
        // 1. 起始時間處理：若為 null 預設為一週前，若有值則維持該時間點（通常是 00:00:00）
        LocalDateTime actualStart = (start != null) ? start : LocalDateTime.now().minusWeeks(1);
        
        // 2. 結束時間處理：
        LocalDateTime actualEnd;
        if (end != null) {
            // 強制轉換為當天的 23:59:59
            actualEnd = end.toLocalDate().atTime(LocalTime.MAX);
        } else {
            // 若沒給結束時間，預設為現在
            actualEnd = LocalDateTime.now();
        }

        if (status != null) {
            return orderRepository.findByCreatedAtBetweenAndStatus(actualStart, actualEnd, status, pageable);
        } else {
            return orderRepository.findByCreatedAtBetween(actualStart, actualEnd, pageable);
        }
    }
    
    @Override
    public Page<Order> getAllOrders(Integer status, Pageable pageable) {
        if (status != null) {
            // 如果有帶狀態，就查特定狀態
            return orderRepository.findByStatus(status, pageable);
        }
        // 沒帶狀態則維持原樣查全部
        return orderRepository.findAll(pageable);
    }
    
    @Override
    public Page<Order> getAllOrders(Pageable pageable) {
        // JPA 會自動處理分頁邏輯
        return orderRepository.findAll(pageable);
    }

    @Override
    public List<Order> getAllOrders() {
        // 直接 findAll()，這就是管理員的權限
        return orderRepository.findAll();
    }

    @Override
    public Order getOrderById(Integer orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("找不到編號為 " + orderId + " 的訂單"));
    }

    @Override
    @Transactional
    public Order updateOrderStatus(Integer orderId, Integer status) {
        Order order = getOrderById(orderId);
        
        // 這裡可以加入業務邏輯檢查，例如「已取消」的訂單不能再改回「已付款」
        order.setStatus(status);
        return orderRepository.save(order);
    }

    @Override
    public List<Order> getOrdersByUserId(Integer userId) {
        return orderRepository.findByUserId(userId);
    }
}