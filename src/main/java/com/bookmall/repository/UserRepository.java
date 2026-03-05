package com.bookmall.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.bookmall.entity.User;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    // 透過帳號找人（用於一般登入）
    Optional<User> findByUsername(String username);

    // 透過 Email 找人（用於 OAuth2 判斷是否已存在帳號）
    Optional<User> findByEmail(String email);

    // 透過 OAuth2 的提供者 ID 找人
    Optional<User> findByProviderId(String providerId);
}