package com.internetcafe.service.impl;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.internetcafe.config.JwtProperties;
import com.internetcafe.dto.response.AuthResponse;
import com.internetcafe.entity.Administrator;
import com.internetcafe.enums.AuditAction;
import com.internetcafe.exception.ForbiddenException;
import com.internetcafe.repository.AdministratorRepository;
import com.internetcafe.security.jwt.JwtService;
import com.internetcafe.security.oauth.GoogleIdTokenVerifierService;
import com.internetcafe.service.admin.audit.AdminAuditLogService;
import com.internetcafe.service.admin.auth.AdminAuthServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminAuthServiceImplTest {

    @Mock GoogleIdTokenVerifierService googleVerifier;
    @Mock AdministratorRepository administratorRepository;
    @Mock JwtService jwtService;
    @Mock AdminAuditLogService adminAuditLogService;
    @Mock JwtProperties jwtProperties;
    @Mock HttpServletRequest request;

    @InjectMocks
    AdminAuthServiceImpl adminAuthService;

    private Administrator activeAdmin;

    @BeforeEach
    void setUp() {
        activeAdmin = new Administrator();
        activeAdmin.setId("admin-001");
        activeAdmin.setEmail("admin@test.com");
        activeAdmin.setFullName("Test Admin");
        activeAdmin.setActive(true);
        activeAdmin.setHr(true);
        activeAdmin.setUsername("admin");
    }

    @Test
    void loginWithGoogle_success_issuesTokensAndAudit() {
        GoogleIdToken.Payload payload = new GoogleIdToken.Payload();
        payload.setEmail("admin@test.com");
        payload.setEmailVerified(true);
        payload.setSubject("google-sub-1");

        when(googleVerifier.verify("id-token")).thenReturn(payload);
        when(administratorRepository.findByEmailIgnoreCaseAndIsActiveTrue("admin@test.com"))
                .thenReturn(Optional.of(activeAdmin));
        when(jwtService.createAccessToken(any())).thenReturn("access");
        when(jwtService.createRefreshToken(any())).thenReturn("refresh");
        when(request.getHeader("X-Forwarded-For")).thenReturn(null);
        when(request.getHeader("User-Agent")).thenReturn("JUnit");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");

        AuthResponse response = adminAuthService.loginWithGoogle("id-token", request);

        assertThat(response.getAccessToken()).isEqualTo("access");
        assertThat(response.getRefreshToken()).isEqualTo("refresh");
        verify(administratorRepository).save(activeAdmin);
        verify(adminAuditLogService).record(
                eq(activeAdmin),
                eq(AuditAction.ADMIN_LOGIN_SUCCESS),
                eq("Admin logged in via Google OAuth"),
                eq("127.0.0.1"),
                eq("JUnit")
        );
    }

    @Test
    void loginWithGoogle_unverifiedEmail_forbidden() {
        GoogleIdToken.Payload payload = new GoogleIdToken.Payload();
        payload.setEmailVerified(false);

        when(googleVerifier.verify("id-token")).thenReturn(payload);

        assertThatThrownBy(() -> adminAuthService.loginWithGoogle("id-token", request))
                .isInstanceOf(ForbiddenException.class);
        verifyNoInteractions(adminAuditLogService);
    }

    @Test
    void loginWithGoogle_googleSubMismatch_forbidden() {
        activeAdmin.setGoogleSub("existing-sub");
        GoogleIdToken.Payload payload = new GoogleIdToken.Payload();
        payload.setEmail("admin@test.com");
        payload.setEmailVerified(true);
        payload.setSubject("Different-sub");

        when(googleVerifier.verify("id-token")).thenReturn(payload);
        when(administratorRepository.findByEmailIgnoreCaseAndIsActiveTrue("admin@test.com"))
                .thenReturn(Optional.of(activeAdmin));

        assertThatThrownBy(() -> adminAuthService.loginWithGoogle("id-token", request))
                .isInstanceOf(ForbiddenException.class);
        verify(administratorRepository, never()).save(any());
    }
}
