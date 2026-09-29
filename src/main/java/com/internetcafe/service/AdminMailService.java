package com.internetcafe.service;

public interface AdminMailService {

    void sendPasswordResetEmail(String toEmail, String fullName, String resetLink);
}
