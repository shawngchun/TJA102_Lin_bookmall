package com.bookmall.service;

import org.springframework.security.core.Authentication;

import com.bookmall.dto.AuthResponse;
import com.bookmall.dto.RegisterRequest;

public interface AuthService {
	/**
     * 處理使用者註冊邏輯
     */
    AuthResponse register(RegisterRequest request);

    /**
     * 處理使用者登入邏輯（手動認證）
     */    
    Authentication authenticate(String email, String password);
    
    // 處理忘記密碼請求 (寄送 Email)
    AuthResponse processForgotPassword(String email);
    
    // 執行重設密碼
    AuthResponse updatePassword(String token, String newPassword);
}
