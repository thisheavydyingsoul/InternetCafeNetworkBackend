package com.internetcafe.service;

import java.time.Duration;
import java.util.Optional;

public interface AdminPasswordResetTokenStore {

    void save(String token, String administratorId, Duration ttl);

    Optional<String> findAdministratorId(String token);

    boolean delete(String token);

    boolean tryAcquireRateLimit(String email, Duration window);
}
