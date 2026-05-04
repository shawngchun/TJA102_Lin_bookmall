package com.bookmall.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookmall.dto.ChangePasswordRequest;
import com.bookmall.entity.BkmlUser;
import com.bookmall.repository.BkmlUserRepository;
import com.bookmall.service.BkmluserService;

@Service
public class BkmluserServiceImpl implements BkmluserService{
	
	@Autowired
    private BkmlUserRepository userRepository;
	
	@Autowired
    private PasswordEncoder passwordEncoder; // 注入 SecurityConfig 裡的 Bean

	@Override
	public BkmlUser findByEmail(String email) {
		
		Optional<BkmlUser> userOpt = userRepository.findByEmail(email);
		
		BkmlUser currentUser = userOpt.orElse(null);
		
		return currentUser;
	}

	@Override
	@Transactional
	public void changePassword(String email, ChangePasswordRequest request) {
		// 1. 尋找使用者[cite: 1, 5]
        BkmlUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("用戶不存在"));

        // 2. 驗證舊密碼是否正確
        // 注意：必須用 passwordEncoder.matches()，不能直接字串比對
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("舊密碼輸入錯誤");
        }

        // 3. 驗證兩次新密碼是否一致
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("兩次輸入的新密碼不符");
        }

        // 4. 加密新密碼並儲存
        String encodedPassword = passwordEncoder.encode(request.getNewPassword());
        user.setPassword(encodedPassword);
        userRepository.save(user);
	}
	
	@Override
	@Transactional
	public void updateUsername(String email, String newUsername) {
	    // 1. 取得當前用戶[cite: 5]
	    BkmlUser user = userRepository.findByEmail(email)
	            .orElseThrow(() -> new RuntimeException("用戶不存在"));

	    // 2. 檢查新名字是否與舊的一樣 (若一樣則不處理)
	    if (newUsername.equals(user.getUsername())) {
	        return;
	    }

	    // 3. 更新並儲存
	    user.setUsername(newUsername);
	    userRepository.save(user);
	}

}
