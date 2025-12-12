package com.santa4you.santa_delivery_service.service;

import com.santa4you.santa_delivery_service.model.VerificationToken;
import com.santa4you.santa_delivery_service.repository.VerificationTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    @Mock
    private VerificationTokenRepository tokenRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private TokenService tokenService;

    private String testEmail;

    @BeforeEach
    void setUp() {
        testEmail = "abc@gsail.com";
    }

    @Test
    void generateAndSendToken_ShouldSaveTokenToRepository() {
        ArgumentCaptor<VerificationToken> tokenCaptor = ArgumentCaptor.forClass(VerificationToken.class);
        when(tokenRepository.save(any(VerificationToken.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        tokenService.generateAndSendToken(testEmail);

        verify(tokenRepository, times(1)).save(tokenCaptor.capture());
        VerificationToken savedToken = tokenCaptor.getValue();
        
        assertThat(savedToken.getEmail()).isEqualTo(testEmail);
        assertThat(savedToken.getCode()).hasSize(6);
        assertThat(savedToken.getExpiryDate()).isAfter(LocalDateTime.now());
        assertThat(savedToken.isUsed()).isFalse();
    }

    @Test
    void generateAndSendToken_ShouldSendEmailWithToken() {
        when(tokenRepository.save(any(VerificationToken.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        String generatedToken = tokenService.generateAndSendToken(testEmail);

        verify(emailService, times(1)).sendVerificationEmail(testEmail, generatedToken);
    }

    @Test
    void verifyToken_WithValidToken_ShouldReturnTrue() {
        String token = "123456";
        VerificationToken verificationToken = new VerificationToken(
                token,
                testEmail,
                LocalDateTime.now().plusMinutes(10)
        );
        
        when(tokenRepository.findByCode(token)).thenReturn(Optional.of(verificationToken));
        when(tokenRepository.save(any(VerificationToken.class))).thenReturn(verificationToken);

        boolean result = tokenService.verifyToken(testEmail, token);

        assertThat(result).isTrue();
    }

    @Test
    void verifyToken_WithExpiredToken_ShouldReturnFalse() {
        String token = "123456";
        VerificationToken expiredToken = new VerificationToken(
                token,
                testEmail,
                LocalDateTime.now().minusMinutes(10)
        );
        
        when(tokenRepository.findByCode(token)).thenReturn(Optional.of(expiredToken));

        boolean result = tokenService.verifyToken(testEmail, token);

        assertThat(result).isFalse();
        verify(tokenRepository, never()).save(any());
    }

    @Test
    void verifyToken_WithAlreadyUsedToken_ShouldReturnFalse() {
        String token = "123456";
        VerificationToken usedToken = new VerificationToken(
                token,
                testEmail,
                LocalDateTime.now().plusMinutes(10)
        );
        usedToken.setUsed(true);
        
        when(tokenRepository.findByCode(token)).thenReturn(Optional.of(usedToken));

        boolean result = tokenService.verifyToken(testEmail, token);

        assertThat(result).isFalse();
    }

    @Test
    void verifyToken_WithWrongEmail_ShouldReturnFalse() {
        String token = "123456";
        String inCorrectEmail = "wrong@example.com";
        VerificationToken verificationToken = new VerificationToken(
                token,
                testEmail,
                LocalDateTime.now().plusMinutes(10)
        );
        
        when(tokenRepository.findByCode(token)).thenReturn(Optional.of(verificationToken));

        boolean result = tokenService.verifyToken(inCorrectEmail, token);

        assertThat(result).isFalse();
    }

    @Test
    void verifyToken_WithNullToken_ShouldReturnFalse() {
        when(tokenRepository.findByCode(null)).thenReturn(Optional.empty());

        boolean result = tokenService.verifyToken(testEmail, null);

        assertThat(result).isFalse();
    }

    @Test
    void verifyToken_WithEmptyToken_ShouldReturnFalse() {
        when(tokenRepository.findByCode("")).thenReturn(Optional.empty());

        boolean result = tokenService.verifyToken(testEmail, "");

        assertThat(result).isFalse();
    }
}