package com.internetcafe.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


public class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void baseException_mapsStatusAndCode() {
        BadRequestException ex = new BadRequestException("Bad", "VALIDATION");

        ResponseEntity<Map<String, Object>> response = handler.handleBaseException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsEntry("code", "VALIDATION");
        assertThat(response.getBody()).containsEntry("message", "Bad");
    }

    @Test
    void resourceNotFound_returns404() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Office", "id-1");

        ResponseEntity<Map<String, Object>> response = handler.handleBaseException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).containsEntry("code", "RESOURCE_NOT_FOUND");
    }
}
