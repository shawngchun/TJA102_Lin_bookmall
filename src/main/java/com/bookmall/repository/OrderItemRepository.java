package com.bookmall.repository;

import com.bookmall.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {
    // 方便管理員查詢特定訂單下的所有明細
    List<OrderItem> findByOrderId(Integer orderId);
}