package com.bookmall.dto;

public class CartAddRequest {
    private Integer bookId;
    private Integer quantity;
    
    public CartAddRequest() {}
    
	// 務必提供 Getter 和 Setter，Spring 才能注入資料
    public Integer getBookId() { return bookId; }
    public void setBookId(Integer bookId) { this.bookId = bookId; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}