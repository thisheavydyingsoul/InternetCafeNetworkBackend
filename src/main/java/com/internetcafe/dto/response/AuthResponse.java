package com.internetcafe.dto.response;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AuthResponse {
    String accessToken;
    String refreshToken;
    String tokenType;
    long expiresInMs;
    AdminProfileResponse admin;
}
