package com.internetcafe.dto.request.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AdminForgotPasswordRequest (
        @NotBlank @Email String email
        ) {}
