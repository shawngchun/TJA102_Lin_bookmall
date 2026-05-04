package com.bookmall.controller;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookmall.dto.CartAddRequest;
import com.bookmall.dto.CartItemDto;
import com.bookmall.service.BookService;
import com.bookmall.service.CartService;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;
    
    @Autowired
    private BookService bookService;

    // 1. 加入商品到購物車
    // POST http://localhost:8080/api/cart/add?bookId=1&quantity=1
    @PostMapping("/add")
    public Map<String, String> addToCart(
    		Principal principal,
    		@RequestBody CartAddRequest cartAddRequest) {

        // 從 Security 直接拿到當前登入的 username，這裡從UserDetails拿的username實際上是BkmlUser的email
        
        cartService.addBookToCart(principal.getName(), cartAddRequest.getBookId(), cartAddRequest.getQuantity());
        String bookName = bookService.getBookById(cartAddRequest.getBookId()).getTitle();
 
        return Map.of("message", "已將書籍 " + bookName + " 加入購物車", "user", principal.getName());
    }

    // 2. 查看我的購物車
    // GET http://localhost:8080/api/cart
    @GetMapping
    public List<CartItemDto> getMyFullCart(Principal principal) {
    	// 這裡從UserDetails拿的username實際上是BkmlUser的email
    	return cartService.getCartDetails(principal.getName());
    }

    // 3. 從購物車移除商品
    // DELETE http://localhost:8080/api/cart/remove/{bookId}
    @DeleteMapping("/remove/{bookId}")
    public Map<String, String> removeFromCart(
    		Principal principal,
            @PathVariable Integer bookId) {
        
    	// 這裡從UserDetails拿的username實際上是BkmlUser的email
        cartService.removeFromCart(principal.getName(), bookId);
        return Map.of("message", "已移除書籍 " + bookId);
    }
    
    @GetMapping("/count")
    public Integer getCartItemNum(Principal principal) {
    	// 這裡從UserDetails拿的username實際上是BkmlUser的email
    	return cartService.getCartItemNum(principal.getName());
    }
}