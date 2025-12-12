package com.santa4you.santa_delivery_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith({MockitoExtension.class, OutputCaptureExtension.class})
class EmailServiceTest {

    @InjectMocks
    private EmailService emailService;

    private String testEmail;
    private String testToken;

    @BeforeEach
    void setUp() {
        testEmail = "abc@gsail.com";
        testToken = "123456";
    }

    @Test
    void sendVerificationEmail_ShouldLogEmailDetails(CapturedOutput output) {
        emailService.sendVerificationEmail(testEmail, testToken);

        String logOutput = output.toString();
        assertThat(logOutput).contains("SIMULATED EMAIL TO: " + testEmail);
        assertThat(logOutput).contains("Your verification code is: " + testToken);
        assertThat(logOutput).contains("This code will expire in 10 minutes");
    }

    @Test
    void sendVerificationEmail_ShouldLogSubject(CapturedOutput output) {
        emailService.sendVerificationEmail(testEmail, testToken);

        assertThat(output.toString()).contains("Subject: Santa4You - Verification Code");
    }
}