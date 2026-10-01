package com.internetcafe.service.impl;

import com.internetcafe.config.AppEmailProperties;
import com.internetcafe.service.AdminMailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminMailServiceImpl implements AdminMailService {

    private final JavaMailSender mailSender;
    private final AppEmailProperties emailProperties;

    @Override
    public void sendPasswordResetEmail(String toEmail, String fullName, String resetLink) {
        if (emailProperties.getFrom() == null || emailProperties.getFrom().isBlank()) {
            log.warn("MAIL_FROM / app.email.from is empty; skipping password reset email to {}", toEmail);
            return;
        }
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(emailProperties.getFrom());
            helper.setTo(toEmail);
            helper.setSubject("Reset your administrator password");
            String safeName = fullName == null ? "Administrator" : fullName;
            helper.setText("""
                    Hello %s,
                    
                    We received a request to reset your administrator password.
                    
                    Open this link to set a new password (valid for a limited time):
                    
                    %s
                    
                    If you did not request this, you can ignore this email.
                    
                    - Internet Cafe Admin
                    """.formatted(safeName, resetLink), false);
            mailSender.send(message);
        } catch (MessagingException ex) {
            log.error("Failed to send password reset email to {}", toEmail, ex);
            throw new IllegalStateException("Failed to send email", ex);
        }
    }
}
