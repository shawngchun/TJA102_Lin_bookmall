package com.bookmall.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/search", "/css/**", "/js/**").permitAll() // 允許所有人存取的頁面
                .anyRequest().authenticated() // 其他頁面（如購物車、結帳）仍需登入
            )
            .formLogin(login -> login.permitAll()) // 保留登入表單
            .logout(logout -> logout.permitAll());
            
        return http.build();
    }
}