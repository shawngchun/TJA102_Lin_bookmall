package com.bookmall.dto;

public class UserNavDTO {
	
	private String username;
    private String pictureUrl;
    
	public UserNavDTO() {}
	
	public UserNavDTO(String username, String pictureUrl) {
		super();
		this.username = username;
		this.pictureUrl = pictureUrl;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPictureUrl() {
		return pictureUrl;
	}

	public void setPictureUrl(String pictureUrl) {
		this.pictureUrl = pictureUrl;
	}
    
    
}
