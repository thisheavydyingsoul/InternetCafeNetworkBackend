package com.internetcafe.security.jwt;

import com.internetcafe.config.JwtProperties;
import com.internetcafe.exception.UnauthorizedException;
import com.internetcafe.security.oauth.AdminPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class JwtService {

    private static final String CLAIM_TYPE = "type";
    private static final String CLAIM_HR = "hr";
    private static final String TYPE_ACCESS = "access";
    private static final String TYPE_REFRESH = "refresh";

    private final JwtProperties jwtProperties;

    public String createAccessToken(AdminPrincipal principal) {
        return buildToken(
                principal.getId(),
                principal.getEmail(),
                principal.isHr(),
                TYPE_ACCESS,
                jwtProperties.getExpiration()
        );
    }

    public String createRefreshToken(AdminPrincipal principal) {
        return buildToken(
                principal.getId(),
                principal.getEmail(),
                principal.isHr(),
                TYPE_REFRESH,
                jwtProperties.getRefreshExpiration()
        );
    }

    public AdminPrincipal parseAccessToken(String token) {
        Claims claims = parseClaims(token);
        if (!TYPE_ACCESS.equals(claims.get(CLAIM_TYPE, String.class))) {
            throw new UnauthorizedException("Invalid access token", "INVALID_ACCESS");
        }
        return toPrincipal(claims);
    }

    public AdminPrincipal parseRefreshToken(String token) {
        Claims claims = parseClaims(token);
        if (!TYPE_REFRESH.equals(claims.get(CLAIM_TYPE, String.class))) {
            throw new UnauthorizedException("Invalid refresh token", "INVALID_REFRESH");
        }
        return toPrincipal(claims);
    }

    private String buildToken(String adminId, String email, boolean hr, String type, long ttlMs) {
        if (jwtProperties.getSecret() == null || jwtProperties.getSecret().length() < 32) {
            throw new IllegalStateException("JWT_SECRET must be at least 32 characters");
        }
        Date now = new Date();
        Date exp = new Date(now.getTime() + ttlMs);
        return Jwts.builder()
                .subject(adminId)
                .claims(Map.of(
                        CLAIM_TYPE, type,
                        "email", email,
                        CLAIM_HR, hr
                ))
                .issuedAt(now)
                .expiration(exp)
                .signWith(jwtSecretKey())
                .compact();
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(jwtSecretKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException | IllegalArgumentException ex) {
            throw new UnauthorizedException("Token validation failed", "INVALID_TOKEN");
        }
    }

    private AdminPrincipal toPrincipal(Claims claims) {
        String id = claims.getSubject();
        String email = claims.get("email", String.class);
        Boolean hr = claims.get(CLAIM_HR, Boolean.class);
        return new AdminPrincipal(id, email, Boolean.TRUE.equals(hr));
    }

    private SecretKey jwtSecretKey() {
        byte[] keyBytes = jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
