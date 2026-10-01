package com.internetcafe.service.impl;

import com.internetcafe.service.AdminPasswordResetTokenStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RedisAdminPasswordResetTokenStoreTest {

    @Mock StringRedisTemplate redis;
    @Mock ValueOperations<String, String> valueOps;

    AdminPasswordResetTokenStore store;

    @BeforeEach
    void setUp() {
        when(redis.opsForValue()).thenReturn(valueOps);
        store = new RedisAdminPasswordResetTokenStore(redis);
    }

    @Test
    void save_writesKeyWithTtl() {
        store.save("token-1", "admin-001", Duration.ofHours(1));

        verify(valueOps).set(eq("admin:pwd-reset:token:token-1"), eq("admin-001"), eq(Duration.ofHours(1)));
    }

    @Test
    void findAdministratorId_returnsOptional() {
        when(valueOps.get("admin:pwd-reset:token:token-1")).thenReturn("admin-001");

        Optional<String> id = store.findAdministratorId("token-1");

        assertThat(id).contains("admin-001");
    }

    @Test
    void tryAcquireRateLimit_delegatesToSetIfAbsent() {
        when(valueOps.setIfAbsent(eq("admin:pwd-reset:rate:a@b.com"), eq("1"), any(Duration.class)))
                .thenReturn(true);

        assertThat(store.tryAcquireRateLimit("a@b.com", Duration.ofSeconds(60))).isTrue();
    }
}
