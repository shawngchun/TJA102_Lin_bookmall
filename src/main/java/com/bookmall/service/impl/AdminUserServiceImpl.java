package com.bookmall.service.impl;

import com.bookmall.entity.BkmlUser;
import com.bookmall.repository.BkmlUserRepository;
import com.bookmall.service.AdminUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminUserServiceImpl implements AdminUserService {

    @Autowired
    private BkmlUserRepository userRepository;

    @Override
    public List<BkmlUser> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public BkmlUser updateUserRole(Integer userId, String newRole) {
        BkmlUser user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("找不到編號為 " + userId + " 的使用者"));
        user.setRole(newRole);
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUser(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("欲刪除的使用者不存在");
        }
        userRepository.deleteById(userId);
    }

    @Override
    public List<BkmlUser> searchUsersByEmail(String email) {
        // 這裡可以呼叫 Repository 的模糊查詢方法，例如 findByUsernameContaining
        return userRepository.findByEmailContaining(email);
    }
}