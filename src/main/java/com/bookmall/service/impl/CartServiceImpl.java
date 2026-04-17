package com.bookmall.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.bookmall.dto.CartItemDto;
import com.bookmall.repository.BookRepository;
import com.bookmall.service.CartService;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private StringRedisTemplate redisTemplate;
    
    @Autowired
    private BookRepository bookRepository; // 注入 BookRepo 來查資料庫

    private static final String CART_PREFIX = "cart:";

    @Override
    public void addBookToCart(String username, Integer bookId, Integer quantity) {
        String key = CART_PREFIX + username;
        // 使用 Redis Hash：HINCRBY (如果書已存在，數量累加)
        redisTemplate.opsForHash().increment(key, String.valueOf(bookId), quantity);
    }

    @Override
    public Map<Object, Object> getCart(String username) {
        return redisTemplate.opsForHash().entries(CART_PREFIX + username);
    }

    @Override
    public void removeFromCart(String username, Integer bookId) {
        redisTemplate.opsForHash().delete(CART_PREFIX + username, String.valueOf(bookId));
    }
    
    @Override
    public List<CartItemDto> getCartDetails(String username) {
        Map<Object, Object> rawCart = redisTemplate.opsForHash().entries(CART_PREFIX + username);
        List<CartItemDto> details = new ArrayList<>();

        for (Map.Entry<Object, Object> entry : rawCart.entrySet()) {
            Integer bookId = Integer.parseInt(entry.getKey().toString());
            Integer quantity = Integer.parseInt(entry.getValue().toString());

            bookRepository.findById(bookId).ifPresent(book -> {
                // 使用我們新寫的構造函數，把實體轉成 DTO
                details.add(new CartItemDto(book, quantity));
            });
        }
        return details;
    }
    
    @Override
    public void clearCart(String username) {
        String key = CART_PREFIX + username;
        
        // 這裡我們可以直接刪除整個 Key
        // Boolean.TRUE.equals 是為了處理回傳值可能為 null 的包裝類別安全性
        Optional.ofNullable(redisTemplate.delete(key))
                .ifPresent(success -> System.out.println("購物車清空狀態: " + success));
    }

	@Override
	public Integer getCartItemNum(String username) {
		
		Map<Object, Object> rawCart = redisTemplate.opsForHash().entries(CART_PREFIX + username);
		Integer totalQuantity = 0;
		
		for (Map.Entry<Object, Object> entry: rawCart.entrySet()) {
			Integer quantity = Integer.parseInt(entry.getValue().toString());
			totalQuantity += quantity;
		}
		return totalQuantity;
	}
}
