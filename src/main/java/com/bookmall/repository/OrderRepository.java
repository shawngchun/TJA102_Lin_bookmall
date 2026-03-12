package com.bookmall.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bookmall.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Integer> {
	List<Order> findByUserId(Integer userId);
}