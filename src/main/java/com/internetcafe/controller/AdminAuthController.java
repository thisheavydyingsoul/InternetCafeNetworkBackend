package com.internetcafe.controller;

import com.internetcafe.dto.request.auth.GoogleLoginRequest;
import com.internetcafe.dto.request.auth.RefreshTokenRequest;
import com.internetcafe.dto.response.AdminProfileResponse;
import com.internetcafe.dto.response.AuthResponse;
import com.internetcafe.security.oauth.AdminPrincipal;
import com.internetcafe.service.AdminAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/admin")
@RequiredArgsConstructor
@Tag(name = "Admin auth", description = "Google OAuth + JWT for admin panel")
public class AdminAuthController {

    private final AdminAuthService adminAuthService;

    @PostMapping("/google")
    @Operation(summary = "Login with Google ID token")
    public ResponseEntity<AuthResponse> loginWithGoogle(
            @Valid @RequestBody GoogleLoginRequest body,
            HttpServletRequest request
            ) {
        return ResponseEntity.ok(adminAuthService.loginWithGoogle(body.idToken(), request));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest body) {
        return ResponseEntity.ok(adminAuthService.refresh(body.refreshToken()));
    }

    @GetMapping("/me")
    @Operation(summary = "Current admin profile")
    public ResponseEntity<AdminProfileResponse> me(@AuthenticationPrincipal AdminPrincipal principal) {
        return ResponseEntity.ok(adminAuthService.me(principal));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout (audit log)")
    public ResponseEntity<Void> logout(
            @AuthenticationPrincipal AdminPrincipal principal,
            HttpServletRequest request
    ) {
        adminAuthService.logout(principal, request);
        return ResponseEntity.noContent().build();
    }
}
