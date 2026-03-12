package com.bookmall.dto;

import java.math.BigDecimal;

public class CartItemDto {
    private Integer bookId;      // 書號
    private String title;        // 書名
    private BigDecimal price;    // 單價
    private String categoryName; // 分類名稱 (從 Book 關聯的 Category 取得)
    private Integer quantity;    // 數量 (來自 Redis)
    private BigDecimal subtotal; // 小計 (單價 * 數量)

    // 空構造函數 (Jackson 需要)
    public CartItemDto() {}

    // 方便的構造函數：直接傳入實體與數量，內部自動拆解
    public CartItemDto(com.bookmall.entity.Book book, Integer quantity) {
        this.bookId = book.getId();
        this.title = book.getTitle();
        this.price = book.getPrice();
        this.quantity = quantity;
        this.subtotal = book.getPrice().multiply(new BigDecimal(quantity));
        
        // 處理分類名稱，防止 Category 為 null 時噴錯
        if (book.getCategory() != null) {
            this.categoryName = book.getCategory().getName();
        }
    }

	public Integer getBookId() {
		return bookId;
	}

	public void setBookId(Integer bookId) {
		this.bookId = bookId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getSubtotal() {
		return subtotal;
	}

	public void setSubtotal(BigDecimal subtotal) {
		this.subtotal = subtotal;
	}
}