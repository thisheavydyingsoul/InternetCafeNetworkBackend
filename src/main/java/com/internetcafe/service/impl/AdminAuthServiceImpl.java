package com.internetcafe.service.impl;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.internetcafe.config.AppAdminProperties;
import com.internetcafe.config.JwtProperties;
import com.internetcafe.dto.response.AdminProfileResponse;
import com.internetcafe.dto.response.AuthResponse;
import com.internetcafe.entity.Administrator;
import com.internetcafe.enums.AuditAction;
import com.internetcafe.exception.ForbiddenException;
import com.internetcafe.exception.UnauthorizedException;
import com.internetcafe.repository.AdministratorRepository;
import com.internetcafe.security.AdminPrincipal;
import com.internetcafe.security.GoogleIdTokenVerifierService;
import com.internetcafe.security.jwt.JwtService;
import com.internetcafe.service.AdminAuditLogService;
import com.internetcafe.service.AdminAuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminAuthServiceImpl implements AdminAuthService {

    private final GoogleIdTokenVerifierService googleVerifier;
    private final AdministratorRepository administratorRepository;
    private final AppAdminProperties appAdminProperties;
    private final JwtService jwtService;
    private final AdminAuditLogService adminAuditLogService;
    private final JwtProperties jwtProperties;

    @Override
    @Transactional
    public AuthResponse loginWithGoogle(String idToken, HttpServletRequest request) {
        GoogleIdToken.Payload payload = googleVerifier.verify(idToken);

        if (!Boolean.TRUE.equals(payload.getEmailVerified())) {
            log.warn("Admin login denieed: email not verified by Google!");
            throw new ForbiddenException("Google email is not verified", "EMAIL_NOT_VERIFIED");
        }

        String email = Optional.ofNullable(payload.getEmail())
                .map(e -> e.toLowerCase(Locale.ROOT))
                .orElseThrow(() -> new UnauthorizedException("Email missing in Google token", "GOOGLE_TOKEN_INVALID"));

        if (!appAdminProperties.allowedEmailSet().contains(email)) {
            log.warn("Admin login denied: email {} not in allowlist", email);
            throw new ForbiddenException("This Google account is not allowed for admin access", "ADMIN_EMAIL_NOT_ALLOWED");
        }

        Administrator admin = administratorRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Admin login denied: no administrator row for {}", email);
                    throw new ForbiddenException("No active administrator for this email", "ADMIN_NOT_FOUND");
                });

        if (!admin.isActive()) {
            log.warn("Admin login denied: inactive admin {}", email);
            throw new ForbiddenException("Administrator account is inactive", "ADMIN_INACTIVE");
        }

        String googleSub = payload.getSubject();
        if (admin.getGoogleSub() == null) {
            admin.setGoogleSub(googleSub);
        } else if (!admin.getGoogleSub().equals(googleSub)) {
            log.warn("Admin login denied: google_sub mismatch for admin {}", email);
            throw new ForbiddenException("Google account does not match registered administrator", "GOOGLE_SUB_MISMATCH");
        }

        admin.setEmailVerified(true);
        admin.setEmailVerifiedAt(LocalDateTime.now());
        admin.setLastLoginAt(LocalDateTime.now());
        administratorRepository.save(admin);

        AdminPrincipal principal = new AdminPrincipal(admin.getId(), admin.getEmail(), admin.isHr());
        String access = jwtService.createAccessToken(principal);
        String refresh = jwtService.createRefreshToken(principal);

        String ip = clientIp(request);
        String ua = request.getHeader("User-Agent");
        adminAuditLogService.record(
                admin,
                AuditAction.ADMIN_LOGIN_SUCCESS,
                "Admin logged in via Google OAuth",
                ip,
                ua
        );
        log.info("Admin login success id={} email={} ip={}", admin.getId(), admin.getEmail(), ip);

        return AuthResponse.builder()
                .accessToken(access)
                .refreshToken(refresh)
                .tokenType("Bearer")
                .expiresInMs(jwtService.createAccessToken(principal).length() > 0 ? 3600000L : 3600000L)
                .admin(toProfile(admin))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse refresh(String refreshToken) {
        AdminPrincipal principal = jwtService.parseRefreshToken(refreshToken);
        Administrator admin = administratorRepository.findById(principal.getId())
                .orElseThrow(() -> new UnauthorizedException("Administrator not found", "ADMIN_NOT_FOUND"));

        if (!admin.isActive()) {
            throw new ForbiddenException("Administrator account is inactive", "ADMIN_INACTIVE");
        }

        AdminPrincipal fresh = new AdminPrincipal(admin.getId(), admin.getEmail(), admin.isHr());
        return AuthResponse.builder()
                .accessToken(jwtService.createAccessToken(fresh))
                .refreshToken(jwtService.createRefreshToken(fresh))
                .tokenType("Bearer")
                .expiresInMs(jwtProperties.getExpiration())
                .admin(toProfile(admin))
                .build();
    }


    @Override
    @Transactional(readOnly = true)
    public AdminProfileResponse me(AdminPrincipal principal) {
        Administrator admin = administratorRepository.findById(principal.getId())
                .orElseThrow(() -> new UnauthorizedException("Administrator not found", "ADMIN_NOT_FOUND"));

        return toProfile(admin);
    }

    @Override
    @Transactional
    public void logout(AdminPrincipal principal, HttpServletRequest request) {
        Administrator admin = administratorRepository.findById(principal.getId())
                .orElseThrow(() -> new UnauthorizedException("Administrator not found", "ADMIN_NOT_FOUND"));

        adminAuditLogService.record(
                admin,
                AuditAction.ADMIN_LOGOUT,
                "Admin logged out",
                clientIp(request),
                request.getHeader("User-Agent")
        );

        log.info("Admin logout id={} email={}", admin.getId(), admin.getEmail());
    }

    private AdminProfileResponse toProfile(Administrator admin) {
        return AdminProfileResponse.builder()
                .id(admin.getId())
                .email(admin.getEmail())
                .fullName(admin.getFullName())
                .hr(admin.isHr())
                .role(admin.isHr() ? "HR_ADMIN" : "ADMIN")
                .build();
    }

    private String clientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
