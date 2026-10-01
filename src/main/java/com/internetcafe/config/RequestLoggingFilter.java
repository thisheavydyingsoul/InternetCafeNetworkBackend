package com.internetcafe.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class RequestLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        if(shouldSkip(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String requestId = UUID.randomUUID().toString().substring(0,8);
        MDC.put("requestId", requestId);
        long start = System.currentTimeMillis();
        ContentCachingResponseWrapper wrapped = new ContentCachingResponseWrapper(response);
        try {
            filterChain.doFilter(request, wrapped);
        } finally {
            long ms = System.currentTimeMillis() - start;
            int status = wrapped.getStatus();
            log.info("http method={} uri={} status={} durationMs={} remote={}",
                    request.getMethod(),
                    request.getRequestURI(),
                    status,
                    ms,
                    request.getRemoteAddr());
            try {
                wrapped.copyBodyToResponse();
            }
            finally {
                MDC.remove("requestId");
            }
        }

    }

    private boolean shouldSkip(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri.startsWith("/api/actuator")
                || uri.startsWith("/api/swagger-ui")
                || uri.startsWith("/api/v3/api-docs");
    }
}
