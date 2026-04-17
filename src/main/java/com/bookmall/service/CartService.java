package com.bookmall.service;

import java.util.List;
import java.util.Map;

import com.bookmall.dto.CartItemDto;

public interface CartService {
    // 加入購物車 (Key 是用戶帳號, Field 是書 ID, Value 是數量)
    void addBookToCart(String username, Integer bookId, Integer quantity);
    
    // 獲取某人的整台購物車
    Map<Object, Object> getCart(String username);
    
    // 移除某項商品
    void removeFromCart(String username, Integer bookId);
    
    List<CartItemDto> getCartDetails(String username);
    
    public void clearCart(String username);
    
    public Integer getCartItemNum(String username);
}