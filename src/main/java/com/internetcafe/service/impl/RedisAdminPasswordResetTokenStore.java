package com.internetcafe.service.impl;

import com.internetcafe.service.AdminPasswordResetTokenStore;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RedisAdminPasswordResetTokenStore implements AdminPasswordResetTokenStore {

    private static final String TOKEN_PREFIX = "admin:pwd-reset:token:";
    private static final String RATE_PREFIX = "admin:pwd-reset:rate:";

    private final StringRedisTemplate redis;

    @Override
    public void save(String token, String administratorId, Duration ttl) {
        redis.opsForValue().set(TOKEN_PREFIX + token, administratorId, ttl);
    }

    @Override
    public Optional<String> findAdministratorId(String token) {
        return Optional.ofNullable(redis.opsForValue().get(TOKEN_PREFIX + token));
    }

    @Override
    public boolean delete(String token) {
        return Boolean.TRUE.equals(redis.delete(TOKEN_PREFIX + token));
    }

    @Override
    public boolean tryAcquireRateLimit(String email, Duration window) {
        String key = RATE_PREFIX + email.trim().toLowerCase();
        Boolean ok = redis.opsForValue().setIfAbsent(key, "1", window);
        return Boolean.TRUE.equals(ok);
    }
}
