package com.bookmall.service;

import java.util.List;

import com.bookmall.dto.CheckoutRequest;
import com.bookmall.entity.Order;

public interface OrderService {
	Order checkout(String username, CheckoutRequest request);
    void payOrder(Integer orderId);
    List<Order> getUserOrders(String username);

}
