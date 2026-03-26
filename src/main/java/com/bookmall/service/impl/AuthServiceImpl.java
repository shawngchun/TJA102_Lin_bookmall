package com.bookmall.service.impl;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bookmall.dto.AuthResponse;
import com.bookmall.dto.LoginRequest;
import com.bookmall.dto.RegisterRequest;
import com.bookmall.entity.BkmlUser;
import com.bookmall.repository.BkmlUserRepository;
import com.bookmall.service.AuthService;

@Service
public class AuthServiceImpl implements AuthService {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private BkmlUserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

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

		userRepository.save(user);
		return new AuthResponse("註冊成功！", true, user.getUsername());
	}

	@Override
	public AuthResponse login(LoginRequest request) {
		// 1. 將前端傳來的帳密封裝成 Authentication 標記 (尚未認證)
		UsernamePasswordAuthenticationToken unauthenticatedToken = new UsernamePasswordAuthenticationToken(
				request.getEmail(), request.getPassword());

		// 2. 手動委派給主管 (AuthenticationManager) 進行認證
		// 此步驟會自動呼叫 UserDetailsService 與 PasswordEncoder 進行比對
		Authentication authentication = authenticationManager.authenticate(unauthenticatedToken);

		// 3. 認證成功後，將結果存入 SecurityContextHolder
		// 這是手動模式下讓 Session 生效的關鍵步驟
		SecurityContextHolder.getContext().setAuthentication(authentication);

		return new AuthResponse("登入成功", true, authentication.getName());
	}

	@Override
	public AuthResponse processForgotPassword(String email) {
		return userRepository.findByEmail(email).map(user -> {
	        // 1. 產生 UUID 作為 Token
	        String token = UUID.randomUUID().toString();
	        user.setResetToken(token);
	        
	        // 2. 設定過期時間 (例如 15 分鐘後)
	        user.setTokenExpiration(LocalDateTime.now().plusMinutes(15));
	        userRepository.save(user);

	        // TODO: 串接 EmailService 寄送包含此 token 的連結
	        System.out.println("重設連結為: /reset-password.html?token=" + token);
	        
	        return new AuthResponse("重設連結已寄至信箱", true);
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