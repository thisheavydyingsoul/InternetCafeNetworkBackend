package com.internetcafe.service.impl;

import com.internetcafe.config.AppEmailProperties;
import com.internetcafe.entity.Administrator;
import com.internetcafe.enums.AuditAction;
import com.internetcafe.exception.BadRequestException;
import com.internetcafe.repository.AdministratorRepository;
import com.internetcafe.service.AdminAuditLogService;
import com.internetcafe.service.AdminMailService;
import com.internetcafe.service.AdminPasswordResetTokenStore;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Duration;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminPasswordResetServiceImplTest {

    @Mock AdministratorRepository administratorRepository;
    @Mock AdminPasswordResetTokenStore tokenStore;
    @Mock AdminMailService adminMailService;
    @Mock AppEmailProperties emailProperties;
    @Mock PasswordEncoder passwordEncoder;
    @Mock AdminAuditLogService adminAuditLogService;
    @Mock HttpServletRequest request;

    @InjectMocks AdminPasswordResetServiceImpl service;

    private Administrator admin;

    @BeforeEach
    void setUp() {
        admin = new Administrator();
        admin.setId("admin-001");
        admin.setEmail("admin@test.com");
        admin.setFullName("Admin");
        admin.setActive(true);
        admin.setUsername("admin");
    }

    private void stubRequestForAudit() {
        when(request.getHeader("X-Forwarded-For")).thenReturn(null);
        when(request.getHeader("User-Agent")).thenReturn("JUnit");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
    }

    @Test
    void requestReset_rateLimited_doesNothing() {
        when(tokenStore.tryAcquireRateLimit(eq("admin@test.com"), any(Duration.class))).thenReturn(false);

        service.requestReset("admin@test.com", request);

        verifyNoInteractions(administratorRepository, adminMailService, adminAuditLogService);
    }

    @Test
    void requestReset_knownAdmin_savesTokenAndAudits() {
        stubRequestForAudit();
        when(tokenStore.tryAcquireRateLimit(anyString(), any(Duration.class))).thenReturn(true);
        when(request.getHeader("X-Forwarded-For")).thenReturn(null);
        when(administratorRepository.findByEmailIgnoreCaseAndIsActiveTrue("admin@test.com"))
                .thenReturn(Optional.of(admin));

        service.requestReset("admin@test.com", request);

        verify(tokenStore).save(anyString(), eq("admin-001"), any(Duration.class));
        verify(adminMailService).sendPasswordResetEmail(eq("admin@test.com"), eq("Admin"), contains("reset-password?token="));
        verify(adminAuditLogService).record(
                eq(admin),
                eq(AuditAction.ADMIN_PASSWORD_RESET_REQUESTED),
                eq("Password reset requested"),
                eq("127.0.0.1"),
                eq("JUnit")
        );
    }

    @Test
    void confirmReset_invalidToken_badRequest() {
        when(tokenStore.findAdministratorId("bad")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.confirmReset("bad", "newPassword1", request))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void confirmReset_success_updatesPasswordAndDeletesToken() {
        stubRequestForAudit();
        when(tokenStore.findAdministratorId("tok")).thenReturn(Optional.of("admin-001"));
        when(administratorRepository.findById("admin-001")).thenReturn(Optional.of(admin));
        when(passwordEncoder.encode("newPassword1")).thenReturn("hash");

        service.confirmReset("tok", "newPassword1", request);

        verify(administratorRepository).save(admin);
        assert admin.getPasswordHash().equals("hash");
        verify(tokenStore).delete("tok");
        verify(adminAuditLogService).record(
                eq(admin),
                eq(AuditAction.ADMIN_PASSWORD_RESET_COMPLETED),
                eq("Password reset completed"),
                eq("127.0.0.1"),
                eq("JUnit")
        );
    }
}
