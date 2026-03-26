package com.bookmall.dto;

import jakarta.validation.constraints.NotBlank;

public class LoginRequest {
	
    @NotBlank(message = "帳號不能為空")
    private String email;

    @NotBlank(message = "密碼不能為空")
    private String password;

    // 空建構子 (JSON 反序列化必需)
    public LoginRequest() {}

    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // Getter & Setter
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}