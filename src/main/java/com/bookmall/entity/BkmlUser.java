package com.bookmall.entity;

import jakarta.persistence.*;

@Entity
@Table(name="users")
public class BkmlUser {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String username;

    // 配合資料庫設計，密碼設為可為空
    private String password;

    @Column(unique = true)
    private String email;

    private String role; // 預設為 ROLE_USER

    // OAuth2 相關欄位
    private String provider; // LOCAL, GOOGLE, GITHUB
    
    @Column(name = "provider_id")
    private String providerId;

    @Column(name = "picture_url")
    private String pictureUrl;

    // JPA 需要無參數建構子
    public BkmlUser() {
    }

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public String getProviderId() {
		return providerId;
	}

	public void setProviderId(String providerId) {
		this.providerId = providerId;
	}

	public String getPictureUrl() {
		return pictureUrl;
	}

	public void setPictureUrl(String pictureUrl) {
		this.pictureUrl = pictureUrl;
	}
    
    
	
}
