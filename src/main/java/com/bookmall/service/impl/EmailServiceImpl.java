package com.bookmall.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.bookmall.service.EmailService;

@Service
public class EmailServiceImpl implements EmailService{
	
	@Autowired
    private JavaMailSender mailSender;

    public void sendResetPasswordEmail(String toEmail, String token) {
        String resetLink = "http://localhost:8080/reset-password.html?token=" + token;
        
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("your-email@gmail.com");
        message.setTo(toEmail);
        message.setSubject("【幻象書屋】重設您的帳號密碼");
        message.setText("您好：\n\n我們收到了重設密碼的請求。請點擊下方連結進行重設，連結將於 15 分鐘後過期：\n\n" 
                        + resetLink + "\n\n如果您並未要求重設密碼，請忽略此郵件。");

        mailSender.send(message);
    }

}
