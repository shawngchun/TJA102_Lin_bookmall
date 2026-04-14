package com.bookmall.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bookmall.entity.BkmlUser;
import com.bookmall.repository.BkmlUserRepository;
import com.bookmall.service.BkmluserService;

@Service
public class BkmluserServiceImpl implements BkmluserService{
	
	@Autowired
    private BkmlUserRepository userRepository;

	@Override
	public BkmlUser findByEmail(String email) {
		
		Optional<BkmlUser> userOpt = userRepository.findByEmail(email);
		
		BkmlUser currentUser = userOpt.orElse(null);
		
		return currentUser;
	}

}
