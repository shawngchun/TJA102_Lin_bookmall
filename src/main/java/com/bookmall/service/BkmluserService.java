package com.bookmall.service;

import com.bookmall.entity.BkmlUser;

public interface BkmluserService {
	
	BkmlUser findByEmail(String email);

}
