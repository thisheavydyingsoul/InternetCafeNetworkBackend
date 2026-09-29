package com.internetcafe.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.email")
public class AppEmailProperties {

    private String from;
    private String adminFrontendUrl;
    private int verificationTtlHours = 24;
    private int passwordResetTtlHours = 1;
    private int resendRateLimitSeconds = 60;
}
