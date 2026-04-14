package com.bookmall.service;

public interface EmailService {
	
	public void sendResetPasswordEmail(String toEmail, String token);

}
