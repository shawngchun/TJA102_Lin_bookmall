package com.bookmall.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController // 改用 RestController，回傳 JSON 而非頁面
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
 // 新增注入
 	@Autowired
 	private SecurityContextRepository securityContextRepository;

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
//    @PostMapping("/login")
//    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request, HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
//        return ResponseEntity.ok(authService.login(request, servletRequest, servletResponse));
//    }
    
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
        @RequestBody LoginRequest request, 
        HttpServletRequest servletRequest, 
        HttpServletResponse servletResponse) {

        // 1. 呼叫 Service 拿回認證結果
        Authentication authentication = authService.authenticate(request.getEmail(), request.getPassword());

        // 2. 在 Web 層處理 Context (這是 Controller 應該知道的環境操作)
        SecurityContext context = SecurityContextHolder.createEmptyContext();  // 空白身分證
        context.setAuthentication(authentication);  // 填寫空白身分證
        SecurityContextHolder.setContext(context);

        // 3. 在 Web 層處理持久化 (因為這需要 request/response)
        securityContextRepository.saveContext(context, servletRequest, servletResponse);

        return ResponseEntity.ok(new AuthResponse("登入成功", true, authentication.getName()));
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