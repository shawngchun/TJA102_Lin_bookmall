package com.bookmall.dto;

import java.math.BigDecimal;

public class OrderItemDTO {
    private Integer id;
    private Integer bookId;
    private String bookTitle; // 新增：用於存放書名
    private Integer quantity;
    private BigDecimal currentPrice;
    private BigDecimal subtotal; // 可選：在後端先算好小計

    // 建構子、Getter 與 Setter
    public OrderItemDTO(Integer id, Integer bookId, String bookTitle, Integer quantity, BigDecimal currentPrice) {
        this.id = id;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.quantity = quantity;
        this.currentPrice = currentPrice;
        this.subtotal = currentPrice.multiply(new BigDecimal(quantity));
    }

    // ... Getter and Setter ...
    public Integer getId() { return id; }
    public String getBookTitle() { return bookTitle; }
    public Integer getBookId() { return bookId; }
    public Integer getQuantity() { return quantity; }
    public BigDecimal getCurrentPrice() { return currentPrice; }
    public BigDecimal getSubtotal() { return subtotal; }
}