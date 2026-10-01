package com.internetcafe.controller;

import com.internetcafe.dto.request.auth.AdminForgotPasswordRequest;
import com.internetcafe.dto.request.auth.AdminResetPasswordRequest;
import com.internetcafe.service.admin.password.AdminPasswordResetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/admin/password-reset")
@RequiredArgsConstructor
@Tag(name = "Admin password reset")
public class AdminPasswordResetController {

    private final AdminPasswordResetService passwordResetService;

    @PostMapping("/forgot")
    @Operation(summary = "Request password reset email (always 204)")
    public ResponseEntity<Void> forgot(
            @Valid @RequestBody AdminForgotPasswordRequest body,
            HttpServletRequest request
    ) {
        passwordResetService.requestReset(body.email(), request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/validate")
    @Operation(summary = "Validate reset token")
    public ResponseEntity<Void> validate(@RequestParam("token") String token) {
        passwordResetService.validateToken(token);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/confirm")
    @Operation(summary = "Confirm new password")
    public ResponseEntity<Void> confirm(
            @Valid @RequestBody AdminResetPasswordRequest body,
            HttpServletRequest request
            ) {
        passwordResetService.confirmReset(body.token(), body.newPassword(), request);
        return ResponseEntity.noContent().build();
    }
}
