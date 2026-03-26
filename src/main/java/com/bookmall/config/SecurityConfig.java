package com.bookmall.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.bookmall.security.CustomOAuth2UserService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // 沒加這個，Controller 上的 @PreAuthorize 會被無視
public class SecurityConfig {

	@Autowired
    private CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
            	// 開放首頁、靜態資源與所有 API 認證接口 (包含忘記密碼)
                .requestMatchers("/", "/index.html", "/static/**", "/css/**", "/js/**").permitAll()
                .requestMatchers("/api/auth/**", "/api/payment/callback").permitAll()
//                .requestMatchers("/", "/login/**", "/oauth2/**", "/api/payment/callback").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            // 開啟 Http Basic 支援，Postman 的認證才會被讀取
            .httpBasic(Customizer.withDefaults())
            /* 因為你現在使用 AuthController 處理 JSON 登入，
            可以考慮移除 .formLogin()，或者保留它作為傳統頁面備援。
            但為了純 RESTful 體驗，通常會關閉它或自定義 EntryPoint。
            */
            // 傳統表單登入
//            .formLogin(form -> form
//                .loginPage("/login") 
//                .permitAll()
//            )
            // OAuth2 登入配置
            .oauth2Login(oauth2 -> oauth2
                .userInfoEndpoint(userInfo -> userInfo
                    .userService(customOAuth2UserService) // 指定我們寫的 Service
                )
                .defaultSuccessUrl("/home", true) // 登入成功後跳轉的地方
            )
            .logout(logout -> logout
            	    .logoutUrl("/api/auth/logout") // 指定登出的 API 路徑
            	    .logoutSuccessUrl("/") // 登出成功後導向首頁
            	    .invalidateHttpSession(true) // 銷毀伺服器端的 Session
            	    .clearAuthentication(true) // 清除 SecurityContext 中的認證資訊
            	    .deleteCookies("JSESSIONID") // 刪除瀏覽器的 Cookie
            	    .permitAll()
            	);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}