package com.santa4you.santa_delivery_service.service;

import com.santa4you.santa_delivery_service.model.VerificationToken;
import com.santa4you.santa_delivery_service.repository.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class TokenService {
    
    private final VerificationTokenRepository tokenRepository;
    
    public String generateAndSendToken(String email) {
        String token = generateSixDigitCode();
        LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(10);
        
        VerificationToken verificationToken = new VerificationToken(token, email, expiryDate);
        tokenRepository.save(verificationToken);
        
        //email service to send token
        return token;
    }
    
    public boolean verifyToken(String email, String token) {
        return tokenRepository.findByCode(token)
                .map(vToken -> {
                    if (vToken.isExpired()) {
                        return false;
                    }
                    if (vToken.isUsed()) {
                        return false;
                    }
                    if (!vToken.getEmail().equals(email)) {
                        return false;
                    }
                    vToken.setUsed(true);
                    tokenRepository.save(vToken);
                    return true;
                })
                .orElse(false);
    }
    
    private String generateSixDigitCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }
}