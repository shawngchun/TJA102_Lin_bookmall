package com.bookmall.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderResponseDto {
    private Integer orderId;
    private BigDecimal totalAmount;
    private Integer status;
    private String merchantTradeNo;
    private LocalDateTime createdAt;
    private List<OrderItemDto> items;

    public static class OrderItemDto {
        private Integer bookId;
        private Integer quantity;
        private BigDecimal currentPrice;
		public OrderItemDto() {}
		public Integer getBookId() {
			return bookId;
		}
		public void setBookId(Integer bookId) {
			this.bookId = bookId;
		}
		public Integer getQuantity() {
			return quantity;
		}
		public void setQuantity(Integer quantity) {
			this.quantity = quantity;
		}
		public BigDecimal getCurrentPrice() {
			return currentPrice;
		}
		public void setCurrentPrice(BigDecimal currentPrice) {
			this.currentPrice = currentPrice;
		}
        
    }

	public OrderResponseDto() {}

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public List<OrderItemDto> getItems() {
		return items;
	}

	public void setItems(List<OrderItemDto> items) {
		this.items = items;
	}

	public String getMerchantTradeNo() {
		return merchantTradeNo;
	}

	public void setMerchantTradeNo(String merchantTradeNo) {
		this.merchantTradeNo = merchantTradeNo;
	}
	
}