package com.internetcafe.dto.response;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AdminProfileResponse {
    String id;
    String email;
    String fullName;
    boolean hr;
    String role;
}
