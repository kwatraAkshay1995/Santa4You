package com.santa4you.santa_delivery_service.service;

import com.santa4you.santa_delivery_service.exception.TokenGenerationException;
import com.santa4you.santa_delivery_service.model.VerificationToken;
import com.santa4you.santa_delivery_service.repository.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {
    
    private final VerificationTokenRepository tokenRepository;
    private final EmailService emailService;
    private final SecureRandom secureRandom = new SecureRandom();

    /**
     * Generates and sends a verification token with automatic retry on duplicate
     */
    @Transactional
    @Retryable(
            retryFor = DataIntegrityViolationException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 100),
            recover = "recoverTokenGeneration"
    )
    public String generateAndSendToken(String email) {
        String token = generateSixDigitCode();
        LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(10);
        
        VerificationToken verificationToken = new VerificationToken(token, email, expiryDate);
        tokenRepository.save(verificationToken);
        
        emailService.sendVerificationEmail(email, token);
        return token;
    }

    @Recover
    public String recoverTokenGeneration(DataIntegrityViolationException e, String email) {
        log.error("Failed to generate unique token all retries exhausted: {}", email, e);
        throw new TokenGenerationException(
                "Unable to generate verification token. Please try again later."
        );
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
        int code = 100000 + secureRandom.nextInt(900000);
        return String.valueOf(code);
    }
}