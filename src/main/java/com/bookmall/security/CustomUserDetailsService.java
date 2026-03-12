package com.bookmall.security;

import com.bookmall.entity.BkmlUser;
import com.bookmall.repository.BkmlUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 這類別的作用是作為橋樑：
 * 將我們自定義的資料表 (BkmlUser) 轉換為 Spring Security 認得的用戶資訊 (UserDetails)
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private BkmlUserRepository bkmlUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. 從資料庫中搜尋使用者，如果找不到就噴出異常
        BkmlUser bkmlUser = bkmlUserRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("找不到使用者: " + username));

        // 2. 建立並回傳 Spring Security 的 User 物件 (UserDetails 的實作類)
        // 注意：這裡使用了 org.springframework.security.core.userdetails.User 的靜態方法
        return User.withUsername(bkmlUser.getUsername())
                   .password(bkmlUser.getPassword())
                   .authorities(bkmlUser.getRole()) // 這裡傳入如 "ROLE_USER" 或 "ROLE_ADMIN"
                   .build();
        // 只要是BkmlUser有的欄位，什麼亂七八糟都可以往這張身分證丟，點數、照片url...都可以
    }
}