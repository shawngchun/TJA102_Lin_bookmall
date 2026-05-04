package com.bookmall.service.impl;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bookmall.dto.AuthResponse;
import com.bookmall.dto.RegisterRequest;
import com.bookmall.entity.BkmlUser;
import com.bookmall.repository.BkmlUserRepository;
import com.bookmall.service.AuthService;
import com.bookmall.service.EmailService;

@Service
public class AuthServiceImpl implements AuthService {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private BkmlUserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private EmailService emailService; // 注入新建立的服務

	@Override
	public AuthResponse register(RegisterRequest request) {
		// 1. 檢查帳號是否重複
		if (userRepository.existsByEmail(request.getEmail())) {
			return new AuthResponse("該帳號已被註冊", false);
		}

		// 2. 建立實體並將密碼雜湊化 (Hash)
		BkmlUser user = new BkmlUser();
		user.setUsername(request.getUsername());
		user.setEmail(request.getEmail());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setRole("ROLE_USER");

		userRepository.save(user);
		return new AuthResponse("註冊成功！", true, user.getUsername());
	}
	
	    @Override
	    public Authentication authenticate(String email, String password) {
	        // Service 只要負責「確認這個人是真的」
	        // 它不需要知道 Session 怎麼存，也不需要知道 Request 長怎樣
	        return authenticationManager.authenticate(
	            new UsernamePasswordAuthenticationToken(email, password)
	        );
	    }

	    @Override
	    public AuthResponse processForgotPassword(String email) {
	        return userRepository.findByEmail(email).map(user -> {
	            // 1. 產生 Token 並設定過期時間
	            String token = UUID.randomUUID().toString();
	            user.setResetToken(token);
	            user.setTokenExpiration(LocalDateTime.now().plusMinutes(15));
	            userRepository.save(user);

	            // 2. 真正寄出郵件
	            try {
	                emailService.sendResetPasswordEmail(user.getEmail(), token);
	                return new AuthResponse("重設連結已寄至您的信箱", true);
	            } catch (Exception e) {
	                return new AuthResponse("郵件發送失敗，請稍後再試", false);
	            }
	        }).orElse(new AuthResponse("找不到此 Email 關聯的帳號", false));
	    }

	@Override
	public AuthResponse updatePassword(String token, String newPassword) {
		return userRepository.findByResetToken(token)
				.filter(user -> user.getTokenExpiration().isAfter(LocalDateTime.now())) // 檢查是否過期
				.map(user -> {
					// 1. 加密新密碼
					user.setPassword(passwordEncoder.encode(newPassword));

					// 2. 清除 Token 避免重複使用
					user.setResetToken(null);
					user.setTokenExpiration(null);
					userRepository.save(user);

					return new AuthResponse("密碼更新成功", true);
				}).orElse(new AuthResponse("Token 無效或已過期", false));
	}
}