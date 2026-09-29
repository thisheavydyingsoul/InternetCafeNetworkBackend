package com.internetcafe.service;

import jakarta.servlet.http.HttpServletRequest;

public interface AdminPasswordResetService {

    void requestReset(String email, HttpServletRequest request);

    void validateToken(String token);

    void confirmReset(String token, String newPassword, HttpServletRequest request);
}
