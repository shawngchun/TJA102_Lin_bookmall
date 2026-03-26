package com.bookmall.service;

import java.util.List;

import com.bookmall.dto.CheckoutRequest;
import com.bookmall.entity.Order;

public interface OrderService {
	Order checkout(String email, CheckoutRequest request);
	// 1. 產生綠界自動跳轉表單 (回傳 HTML 字串)
    String generatePaymentForm(Integer orderId);
    // 2. 原本的 payOrder 留著，給綠界回傳通知時調用
    void payOrder(Integer orderId);
    List<Order> getOrdersByEmail(String email);

}
