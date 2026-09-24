package com.internetcafe.service;

import com.internetcafe.dto.response.AdminProfileResponse;
import com.internetcafe.dto.response.AuthResponse;
import com.internetcafe.security.AdminPrincipal;
import jakarta.servlet.http.HttpServletRequest;

public interface AdminAuthService {
    AuthResponse loginWithGoogle(String idToken, HttpServletRequest request);

    AuthResponse refresh(String refreshToken);

    AdminProfileResponse me(AdminPrincipal principal);

    void logout(AdminPrincipal principal, HttpServletRequest request);
}
