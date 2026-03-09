package com.bookmall.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
        .csrf(csrf -> csrf.disable()) // 關鍵：測試 API 時先關閉 CSRF
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/search", "/css/**", "/js/**").permitAll() // 首頁與資源不需登入
                .requestMatchers("/admin/**").hasRole("ADMIN") // 只有管理員能進後台
                .requestMatchers("/api/auth/**").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .defaultSuccessUrl("/", true) // 登入成功回首頁
                .permitAll()
            )
            .logout(logout -> logout.permitAll());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // 開發階段先使用明文比對，之後再換成 BCrypt
        return NoOpPasswordEncoder.getInstance();
    }
}