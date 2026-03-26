package com.bookmall.security;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.bookmall.entity.BkmlUser;
import com.bookmall.repository.BkmlUserRepository;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private BkmlUserRepository userRepository;

    @Override                 // OAuth2UserRequest：裝著 Google 給的 access_token（這是一張通行證）
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 1. 呼叫父類別方法，取得 Google 回傳的原始用戶資料
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 2. 提取資訊 (Google 的唯一 ID 通常存在 "sub" 欄位)
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String picture = oAuth2User.getAttribute("picture");
        String providerId = oAuth2User.getAttribute("sub");
        String provider = userRequest.getClientRegistration().getRegistrationId().toUpperCase(); // "GOOGLE"

        // 3. 處理資料庫邏輯：自動註冊或更新
        processUser(email, name, picture, provider, providerId);

        return new DefaultOAuth2User(
                oAuth2User.getAuthorities(),  // 日後如果要進階的話，要改從DB拿資料，現在是先限制OAuth2用戶不能當admin才沒事
                oAuth2User.getAttributes(),
                "email" // <--- 這裡強制指定 email 欄位為 NameAttribute
        );
    }

    private void processUser(String email, String name, String picture, String provider, String providerId) {
        Optional<BkmlUser> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            // 資料庫沒這個人，幫他註冊
            BkmlUser newUser = new BkmlUser();
            newUser.setUsername(email); // 以 Email 當作 username
            newUser.setEmail(email);
            newUser.setRole("ROLE_USER"); // 預設權限
            newUser.setProvider(provider);
            newUser.setProviderId(providerId);
            newUser.setPictureUrl(picture);
            // password 留空，因為 OAuth2 使用者不需要密碼比對
            userRepository.save(newUser);
        } else {
            // 已存在，更新頭像或資訊 (可選)
            BkmlUser existingUser = userOpt.get();
            existingUser.setPictureUrl(picture);
            userRepository.save(existingUser);
        }
    }
}