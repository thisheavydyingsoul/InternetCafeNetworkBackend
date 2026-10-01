package com.internetcafe.service.admin.password;

import com.internetcafe.config.AppEmailProperties;
import com.internetcafe.entity.Administrator;
import com.internetcafe.enums.AuditAction;
import com.internetcafe.exception.BadRequestException;
import com.internetcafe.repository.AdministratorRepository;
import com.internetcafe.service.admin.audit.AdminAuditLogService;
import com.internetcafe.service.admin.mail.AdminMailService;
import com.internetcafe.service.admin.password.store.AdminPasswordResetTokenStore;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminPasswordResetServiceImpl implements AdminPasswordResetService {

    private final AdministratorRepository administratorRepository;
    private final AdminPasswordResetTokenStore tokenStore;
    private final AdminMailService adminMailService;
    private final AppEmailProperties emailProperties;
    private final PasswordEncoder passwordEncoder;
    private final AdminAuditLogService adminAuditLogService;

    @Override
    public void requestReset(String email, HttpServletRequest request) {
        String normalized = email.trim();
        Duration rateWindow = Duration.ofSeconds(emailProperties.getResendRateLimitSeconds());
        if (!tokenStore.tryAcquireRateLimit(normalized, rateWindow)) {
            log.info("Password reset request ignored (rate limit) email={}", normalized);
            return;
        }

        administratorRepository.findByEmailIgnoreCaseAndIsActiveTrue(normalized).ifPresent(admin -> {
            String token = UUID.randomUUID().toString();
            Duration ttl = Duration.ofHours(emailProperties.getPasswordResetTtlHours());
            tokenStore.save(token, admin.getId(), ttl);

            String base = emailProperties.getAdminFrontendUrl();
            if(base == null || base.isBlank()) {
                log.warn("app.email.admin-frontend-url is empty; reset link will be broken");
                base = "http://localhost:4300";
            }
            String link = base.replaceAll("/$", "") + "/reset-password?token=" + token;
            adminMailService.sendPasswordResetEmail(admin.getEmail(), admin.getFullName(), link);

            adminAuditLogService.record(
                    admin,
                    AuditAction.ADMIN_PASSWORD_RESET_REQUESTED,
                    "Password reset requested",
                    clientIp(request),
                    request.getHeader("User-Agent")
            );
            log.info("Admin password reset email queued id={} email={}", admin.getId(), admin.getEmail());
        });
    }

    @Override
    public void validateToken(String token) {
        if (token == null || token.isBlank()) {
            throw new BadRequestException("Invalid or expired reset token", "PASSWORD_RESET_TOKEN_INVALID");
        }
        tokenStore.findAdministratorId(token.trim()).orElseThrow(() ->
                new BadRequestException("Invalid or expired reset token", "PASSWORD_RESET_TOKEN_INVALID"));
    }

    @Override
    @Transactional
    public void confirmReset(String token, String newPassword, HttpServletRequest request) {
        String trimmed = token == null ? "" : token.trim();
        String adminId = tokenStore.findAdministratorId(trimmed).orElseThrow(() ->
                new BadRequestException("Invalid or expired access token", "PASSWORD_RESET_TOKEN_INVALID"));

        Administrator admin = administratorRepository.findById(adminId)
                .filter(Administrator::isActive)
                .orElseThrow(() -> new BadRequestException("Invalid or expired reset token", "PASSWORD_RESET_TOKEN_INVALID"));

        admin.setPasswordHash(passwordEncoder.encode(newPassword));
        administratorRepository.save(admin);
        tokenStore.delete(trimmed);

        adminAuditLogService.record(
                admin,
                AuditAction.ADMIN_PASSWORD_RESET_COMPLETED,
                "Password reset completed",
                clientIp(request),
                request.getHeader("User-Agent")
        );
        log.info("Admin password reset completed id={} email={}", admin.getId(), admin.getEmail());
    }

    private static String clientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if(forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
