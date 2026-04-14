package com.bookmall.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}