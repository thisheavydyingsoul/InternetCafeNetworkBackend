package com.internetcafe.security.jwt;

import com.internetcafe.config.JwtProperties;
import com.internetcafe.exception.UnauthorizedException;
import com.internetcafe.security.oauth.AdminPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        JwtProperties props = new JwtProperties();
        props.setSecret("unit-test-jwt-secret-at-least-32-chars");
        props.setExpiration(3_600_000);
        props.setRefreshExpiration(86_400_000);
        jwtService = new JwtService(props);
    }

    @Test
    void createAndParseAccessToken() {
        AdminPrincipal principal = new AdminPrincipal("admin-001", "admin@test.com", true);

        String token = jwtService.createAccessToken(principal);
        AdminPrincipal parsed = jwtService.parseAccessToken(token);

        assertThat(parsed.getId()).isEqualTo("admin-001");
        assertThat(parsed.getEmail()).isEqualTo("admin@test.com");
        assertThat(parsed.isHr()).isTrue();
    }

    @Test
    void refreshTokenCannotBeUsedAsAccessToken() {
        AdminPrincipal principal = new AdminPrincipal("admin-001", "admin@test.com", false);
        String refresh = jwtService.createRefreshToken(principal);

        assertThatThrownBy(() -> jwtService.parseAccessToken(refresh))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    void accessTokenCannotBeUsedAsRefreshToken() {
        AdminPrincipal principal = new AdminPrincipal("admin-001", "admin@test.com", false);
        String access = jwtService.createAccessToken(principal);

        assertThatThrownBy(() -> jwtService.parseRefreshToken(access))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    void rejectsTokenWhenSecretTooShort() {
        JwtProperties props = new JwtProperties();
        props.setSecret("short");
        props.setExpiration(3_600_600);
        props.setRefreshExpiration(86_400_000);
        JwtService service = new JwtService(props);
        AdminPrincipal principal = new AdminPrincipal("admin-001", "admin@test.com", false);

        assertThatThrownBy(() -> service.createAccessToken(principal))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("JWT_SECRET");
    }
}
