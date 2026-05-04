package com.bookmall.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

import com.bookmall.security.CustomOAuth2UserService;

import jakarta.servlet.http.HttpServletResponse;

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
                .requestMatchers("/", "/login.html", "/register.html", "/successRegister.html", "/index.html", "/static/**", "/css/**", "/js/**", "/forgot-password.html", "/reset-password.html").permitAll()
                .requestMatchers("/api/auth/**", "/api/payment/callback", "/api/auth/forgot-password", "/api/books/**", "/api/categories/**").permitAll()
                .requestMatchers("/orders/success").permitAll() // 允許綠界跳轉回來
                .requestMatchers("/api/payment/callback").permitAll() // 允許綠界 Server 通知
                .requestMatchers("/api/admin/**", "/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            // 開啟 Http Basic 支援，Postman 的認證才會被讀取
            .httpBasic(Customizer.withDefaults())
            /* 因為你現在使用 AuthController 處理 JSON 登入，
            可以考慮移除 .formLogin()，或者保留它作為傳統頁面備援。
            但為了純 RESTful 體驗，通常會關閉它或自定義 EntryPoint。
            */
            // OAuth2 登入配置
            .oauth2Login(oauth2 -> oauth2
                .userInfoEndpoint(userInfo -> userInfo
                    .userService(customOAuth2UserService) // 指定我們寫的 Service
                )
                .defaultSuccessUrl("/index.html", true)
                /*    登入成功後跳轉的地方，最一開始的時候是http://localhost:8080/login/oauth2/code/google
                 *    原因： 這是 Spring Security 內建的 OAuth2LoginAuthenticationFilter 專門用來接收
                 *    Google 回傳「授權碼（Authorization Code）」的窗口。如果你在 Google cloud 改成 /home.html
                 *    ，Google 會把驗證碼丟到你的靜態網頁，而你的網頁並不知道該如何處理這串代碼，導致認證中斷。
                 */
            )
            .exceptionHandling(ex -> ex
            	    // 如果是訪問 API 失敗，回傳 401 而非重導向
            	    .authenticationEntryPoint((request, response, authException) -> {
            	        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "未授權，請先登入");
            	    })
            	)
            .securityContext(context -> context
                    .securityContextRepository(securityContextRepository())
                )
            .logout(logout -> logout
            	    .logoutUrl("/api/logout") // 指定登出的 API 路徑
            	    .logoutSuccessUrl("/login.html?logout=true") // 登出成功後導向首頁
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
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    
 // 在 SecurityConfig 類別內新增，將認證放入持久層
    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }
}