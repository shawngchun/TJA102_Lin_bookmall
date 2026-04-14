package com.bookmall.service;

import com.bookmall.dto.OrderItemDTO;
import com.bookmall.entity.OrderItem;
import java.util.List;

public interface OrderItemService {
    List<OrderItem> getAllOrderItems();
    List<OrderItemDTO> getItemsByOrderId(Integer orderId);
    OrderItem getOrderItemById(Integer id);
    void deleteOrderItem(Integer id);
    // 更新明細 (例如管理員修改數量)
    OrderItem updateOrderItem(Integer id, OrderItem newItemData);
}