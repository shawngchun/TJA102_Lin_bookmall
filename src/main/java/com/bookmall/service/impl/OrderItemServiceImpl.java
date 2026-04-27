package com.bookmall.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bookmall.dto.OrderItemDTO;
import com.bookmall.entity.OrderItem;
import com.bookmall.repository.BookRepository;
import com.bookmall.repository.OrderItemRepository;
import com.bookmall.service.OrderItemService;

@Service
public class OrderItemServiceImpl implements OrderItemService {

    @Autowired private OrderItemRepository orderItemRepository;
    @Autowired private BookRepository bookRepository;

    @Override
    public List<OrderItem> getAllOrderItems() {
        return orderItemRepository.findAll();
    }

    @Override
    public List<OrderItemDTO> getItemsByOrderId(Integer orderId) {
        // 1. 抓取原始明細
        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);

        // 2. 將 Entity 轉換為 DTO 並補上書名
        return items.stream().map(item -> {
            // 根據 bookId 找書名
            String title = bookRepository.findById(item.getBook().getId())
                    .map(book -> book.getTitle())
                    .orElse("未知書籍");
            
            return new OrderItemDTO(
                item.getId(),
                item.getBook().getId(),
                title, // 關鍵：在這裡注入書名
                item.getQuantity(),
                item.getCurrentPrice()
            );
        }).collect(Collectors.toList());
    }

    @Override
    public OrderItem getOrderItemById(Integer id) {
        return orderItemRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteOrderItem(Integer id) {
        orderItemRepository.deleteById(id);
    }

    @Override
    public OrderItem updateOrderItem(Integer id, OrderItem newItemData) {
        OrderItem existing = getOrderItemById(id);
        if (existing != null) {
            existing.setQuantity(newItemData.getQuantity());
            existing.setCurrentPrice(newItemData.getCurrentPrice());
            return orderItemRepository.save(existing);
        }
        return null;
    }
}