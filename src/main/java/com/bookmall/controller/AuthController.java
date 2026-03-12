package com.bookmall.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookmall.entity.BkmlUser;
import com.bookmall.repository.BkmlUserRepository;

@RestController // 改用 RestController，回傳 JSON 而非頁面
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private BkmlUserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder; // 注入在 Config 定義的 Bean

    /**
     * POST http://localhost:8080/api/auth/register
     * 接收 JSON 格式的用戶資料
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody BkmlUser user) {
    	System.out.println("==== 收到註冊請求: " + user.getUsername() + " ====");
        // 1. 檢查帳號是否已存在
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("message", "錯誤：帳號已存在！"));
        }
        
        // 關鍵步驟：加密密碼
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword); // 將加密後的字串存回物件

        // 2. 設定預設權限
        user.setRole("ROLE_USER");

        // 3. 儲存到資料庫
        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "註冊成功！", "username", user.getUsername()));
    }
}