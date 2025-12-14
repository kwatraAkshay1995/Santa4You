package com.santa4you.santa_delivery_service.service;

import com.santa4you.santa_delivery_service.model.VerificationToken;
import com.santa4you.santa_delivery_service.repository.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {
    
    private final VerificationTokenRepository tokenRepository;
    private final EmailService emailService;
    
    public String generateAndSendToken(String email) {
        String token = generateSixDigitCode();
        LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(10);
        
        VerificationToken verificationToken = new VerificationToken(token, email, expiryDate);
        tokenRepository.save(verificationToken);
        
        emailService.sendVerificationEmail(email, token);
        return token;
    }

    /**
     * Verifies token with optimistic locking to prevent double-use
     */
    @Transactional
    public boolean verifyToken(String email, String token) {
        try {
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
        } catch (ObjectOptimisticLockingFailureException e) {
            log.warn("Token already verified by concurrent request for email: {}", email);
            return false;
        }

    }
    
    private String generateSixDigitCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }
}