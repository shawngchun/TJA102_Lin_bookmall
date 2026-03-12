package com.bookmall.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bookmall.dto.CartItemDto;
import com.bookmall.service.CartService;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    // 1. 加入商品到購物車
    // POST http://localhost:8080/api/cart/add?bookId=1&quantity=1
    @PostMapping("/add")
    public Map<String, String> addToCart(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam Integer bookId,
            @RequestParam(defaultValue = "1") Integer quantity) {

        // 從 Security 直接拿到當前登入的 username
        String username = userDetails.getUsername();
        cartService.addBookToCart(username, bookId, quantity);

        return Map.of("message", "已將書籍 " + bookId + " 加入購物車", "user", username);
    }

    // 2. 查看我的購物車
    // GET http://localhost:8080/api/cart
//    @GetMapping
//    public Map<Object, Object> getMyCart(@AuthenticationPrincipal UserDetails userDetails) {
//        return cartService.getCart(userDetails.getUsername());
//    }

    // 3. 從購物車移除商品
    // DELETE http://localhost:8080/api/cart/remove/{bookId}
    @DeleteMapping("/remove/{bookId}")
    public Map<String, String> removeFromCart(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Integer bookId) {
        
        cartService.removeFromCart(userDetails.getUsername(), bookId);
        return Map.of("message", "已移除書籍 " + bookId);
    }
    
    @GetMapping
    public List<CartItemDto> getMyFullCart(@AuthenticationPrincipal UserDetails userDetails) {
        return cartService.getCartDetails(userDetails.getUsername());
    }
}