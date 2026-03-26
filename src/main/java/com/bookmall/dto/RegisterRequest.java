package com.bookmall.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterRequest {
    @NotBlank(message = "使用者名稱不能為空")
    @Size(min = 4, max = 20, message = "使用者名稱長度需在 4 到 20 字元之間")
    private String username;

    @NotBlank(message = "密碼不能為空")
    @Size(min = 6, message = "密碼長度至少需要 6 字元")
    private String password;

    @NotBlank(message = "Email 不能為空")
    @Email(message = "Email 格式不正確")
    private String email;

    public RegisterRequest() {}

    // Getter & Setter
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}