package com.bookmall.service;

import com.bookmall.dto.ChangePasswordRequest;
import com.bookmall.entity.BkmlUser;

public interface BkmluserService {
	
	BkmlUser findByEmail(String email);
	
	void changePassword(String email, ChangePasswordRequest request);
	
	public void updateUsername(String email, String newUsername);

}
