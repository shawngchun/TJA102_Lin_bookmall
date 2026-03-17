package com.bookmall.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.bookmall.entity.Order;

public interface AdminOrderService {
	
	Page<Order> getOrdersByCondition(
		    LocalDateTime start, 
		    LocalDateTime end, 
		    Integer status, 
		    Pageable pageable
		);
	
	// 增加 status 參數，若為 null 則查詢全部
    Page<Order> getAllOrders(Integer status, Pageable pageable);
    
	// 改為分頁回傳
    Page<Order> getAllOrders(Pageable pageable);
    
    // 查看全系統所有訂單
    List<Order> getAllOrders();

    // 根據訂單編號查詢單筆詳細資訊
    Order getOrderById(Integer orderId);

    // 修改訂單狀態 (0:待付款, 1:已付款, 2:已取消, 3:已出貨...)
    Order updateOrderStatus(Integer orderId, Integer status);
    
    // 根據使用者 ID 篩選訂單 (管理員查特定人的單)
    List<Order> getOrdersByUserId(Integer userId);
}