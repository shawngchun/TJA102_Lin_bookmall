package com.bookmall.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookmall.dto.AuthResponse;
import com.bookmall.dto.ForgotPasswordRequest;
import com.bookmall.dto.LoginRequest;
import com.bookmall.dto.RegisterRequest;
import com.bookmall.dto.ResetPasswordRequest;
import com.bookmall.service.AuthService;

@RestController // 改用 RestController，回傳 JSON 而非頁面
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private AuthService authService;

    /**
     * 註冊：委派給 Service 處理加密與存檔
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return response.isSuccess() ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
    }

    /**
     * 登入：手動呼叫認證
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
    
    @PostMapping("/forgot-password")
    public ResponseEntity<AuthResponse> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        return ResponseEntity.ok(authService.processForgotPassword(request.getEmail()));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<AuthResponse> resetPassword(@RequestBody ResetPasswordRequest request) {
        return ResponseEntity.ok(authService.updatePassword(request.getToken(), request.getNewPassword()));
    }
}