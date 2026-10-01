package com.internetcafe.service.admin.mail;

public interface AdminMailService {

    void sendPasswordResetEmail(String toEmail, String fullName, String resetLink);
}
