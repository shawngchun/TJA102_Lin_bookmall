package com.bookmall.controller;

import java.security.Principal;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookmall.dto.ChangePasswordRequest;
import com.bookmall.dto.UpdateUsernameRequest;
import com.bookmall.dto.UserNavDTO;
import com.bookmall.entity.BkmlUser;
import com.bookmall.service.BkmluserService;

@RestController
@RequestMapping("/api/user")
public class UserController {
	
	@Autowired BkmluserService bkmluserService;
    
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal Object principal) {
        String email = null;

        if (principal instanceof UserDetails) {
            // 本地登入：通常 username 存的就是 email
            email = ((UserDetails) principal).getUsername();
        } else if (principal instanceof org.springframework.security.oauth2.core.user.OAuth2User) {
            // OAuth2 登入 (Google/GitHub)：從屬性中取得 email
            org.springframework.security.oauth2.core.user.OAuth2User oauth2User = 
                (org.springframework.security.oauth2.core.user.OAuth2User) principal;
            email = oauth2User.getAttribute("email");
        }

        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // 透過 Email 找到資料庫中的使用者資訊
        BkmlUser user = bkmluserService.findByEmail(email); 
        UserNavDTO dto = new UserNavDTO(user.getUsername(), user.getPictureUrl());

        // 回傳與 login.html 風格一致所需的導覽列資料
        return ResponseEntity.ok(dto);
    }
    
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(
            Authentication authentication, // 使用 Authentication 比 Principal 能拿到更多資訊
            @RequestBody ChangePasswordRequest request) {
        
        // 檢查是否為 OAuth2 用戶[cite: 8]
        if (authentication.getPrincipal() instanceof org.springframework.security.oauth2.core.user.OAuth2User) {
            return ResponseEntity.badRequest().body(Map.of("message", "第三方登入用戶無法直接變更密碼"));
        }

        String email = authentication.getName(); //[cite: 1]
        bkmluserService.changePassword(email, request);
        return ResponseEntity.ok(Map.of("message", "密碼變更成功"));
    }
    
    @PatchMapping("/update-username")
    public ResponseEntity<?> updateUsername(
            Principal principal, 
            @RequestBody UpdateUsernameRequest request) {
        
        try {
            // 在你的系統中，principal.getName() 回傳的是 email[cite: 1]
            String email = principal.getName();
            
            if (request.getNewUsername() == null || request.getNewUsername().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "用戶名稱不能為空"));
            }

            bkmluserService.updateUsername(email, request.getNewUsername());
            
            return ResponseEntity.ok(Map.of(
                "message", "用戶名稱更新成功",
                "newUsername", request.getNewUsername()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}