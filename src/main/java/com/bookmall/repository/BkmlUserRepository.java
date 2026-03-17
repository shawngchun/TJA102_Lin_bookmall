package com.bookmall.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bookmall.entity.BkmlUser;

@Repository
public interface BkmlUserRepository extends JpaRepository<BkmlUser, Integer> {

    // 透過帳號找人（用於一般登入）
    Optional<BkmlUser> findByUsername(String username);

    // 透過 Email 找人（用於 OAuth2 判斷是否已存在帳號）
    Optional<BkmlUser> findByEmail(String email);

    // 透過 OAuth2 的提供者 ID 找人
    Optional<BkmlUser> findByProviderId(String providerId);
    
    // Spring 會自動解析為 SELECT * FROM users WHERE username LIKE %?1%
    List<BkmlUser> findByUsernameContaining(String username);
}