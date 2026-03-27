package com.bookmall.service;

import org.springframework.security.core.Authentication;

import com.bookmall.dto.AuthResponse;
import com.bookmall.dto.LoginRequest;
import com.bookmall.dto.RegisterRequest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {
	/**
     * 處理使用者註冊邏輯
     */
    AuthResponse register(RegisterRequest request);

    /**
     * 處理使用者登入邏輯（手動認證）
     */
//    AuthResponse login(LoginRequest request);
    
//    public AuthResponse login(LoginRequest request, HttpServletRequest servletRequest, HttpServletResponse servletResponse);
    
    Authentication authenticate(String email, String password);
    
    // 處理忘記密碼請求 (寄送 Email)
    AuthResponse processForgotPassword(String email);
    
    // 執行重設密碼
    AuthResponse updatePassword(String token, String newPassword);
}
