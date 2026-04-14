package com.bookmall.dto;

import java.math.BigDecimal;

public class BookListDTO {
    private Integer id;
    private String title;
    private String author;
    private BigDecimal price;
    private String categoryName;
    private Integer stock;
    private String pictureUrl;
    
	public BookListDTO() {}
	
	public BookListDTO(Integer id, String title, String author, BigDecimal price, String categoryName, Integer stock,
			String pictureUrl) {
		super();
		this.id = id;
		this.title = title;
		this.author = author;
		this.price = price;
		this.categoryName = categoryName;
		this.stock = stock;
		this.pictureUrl = pictureUrl;
	}

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
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
	public Integer getStock() {
		return stock;
	}
	public void setStock(Integer stock) {
		this.stock = stock;
	}

	public String getPictureUrl() {
		return pictureUrl;
	}

	public void setPictureUrl(String pictureUrl) {
		this.pictureUrl = pictureUrl;
	}
    
    
}