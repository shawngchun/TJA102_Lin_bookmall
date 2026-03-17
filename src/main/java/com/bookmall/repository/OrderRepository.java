package com.bookmall.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.bookmall.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Integer> {
	List<Order> findByUserId(Integer userId);
	
	// 原有的分頁查詢（查詢所有狀態）
    Page<Order> findAll(Pageable pageable);
	
	Page<Order> findByStatus(Integer userId, Pageable pageable);
	
	// 核心方法：日期區間 + 狀態篩選 + 分頁
    // SQL 會解析為：WHERE created_at BETWEEN ?1 AND ?2 AND (?3 IS NULL OR status = ?3)
    Page<Order> findByCreatedAtBetweenAndStatus(
            LocalDateTime start, 
            LocalDateTime end, 
            Integer status, 
            Pageable pageable
    );

    // 如果不考慮狀態，只查日期區間
    Page<Order> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);
}