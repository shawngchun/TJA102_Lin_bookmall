package com.bookmall.dto;

public class AuthResponse {
    private String message;
    private boolean success;
    private String username;

    public AuthResponse() {}

    public AuthResponse(String message, boolean success) {
        this.message = message;
        this.success = success;
    }

    public AuthResponse(String message, boolean success, String username) {
        this.message = message;
        this.success = success;
        this.username = username;
    }

    // Getter & Setter
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}