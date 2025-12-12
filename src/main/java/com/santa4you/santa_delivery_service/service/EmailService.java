package com.santa4you.santa_delivery_service.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {
    
    public void sendVerificationEmail(String email, String token) {
        log.info("*".repeat(60));
        log.info("SIMULATED EMAIL TO: {}", email);
        log.info("Subject: Santa4You - Verification Code");
        log.info("Your verification code is: {}", token);
        log.info("This code will expire in 10 minutes.");
        log.info("*".repeat(60));
    }
}