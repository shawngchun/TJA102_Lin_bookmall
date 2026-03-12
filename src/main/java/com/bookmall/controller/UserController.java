package com.bookmall.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    /**
     * GET http://localhost:8080/api/user/me
     * 功能：回傳目前登入者的帳號與權限
     * 註解 @AuthenticationPrincipal 會自動注入當前登入的 UserDetails
     */
    @GetMapping("/me")
    public Map<String, Object> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
    	System.out.println("==== 登入請求請求:  ====");
        if (userDetails == null) {
            return Map.of("error", "未登入");
        }
        
        return Map.of(
            "username", userDetails.getUsername(),
            "authorities", userDetails.getAuthorities()
        );
    }
}