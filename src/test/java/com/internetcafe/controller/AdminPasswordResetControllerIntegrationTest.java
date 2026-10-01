package com.internetcafe.controller;

import com.internetcafe.dto.request.auth.AdminResetPasswordRequest;
import com.internetcafe.repository.AdministratorRepository;
import com.internetcafe.service.AdminMailService;
import com.internetcafe.support.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
public class AdminPasswordResetControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired AdministratorRepository administratorRepository;
    @Autowired PasswordEncoder passwordEncoder;

    @MockitoBean AdminMailService adminMailService;

    @Test
    void forgotValidateConfirm_flow() throws Exception {
        mockMvc.perform(post("/auth/admin/password-reset/forgot")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"izranovks@gmail.com\"}"))
                .andExpect(status().isNoContent());

        ArgumentCaptor<String> linkCaptor = ArgumentCaptor.forClass(String.class);
        verify(adminMailService).sendPasswordResetEmail(
                anyString(),
                anyString(),
                linkCaptor.capture()
        );

        String link = linkCaptor.getValue();
        String token = link.substring(link.indexOf("token=") + "token=".length());

        mockMvc.perform(post("/auth/admin/password-reset/validate").param("token", token))
                .andExpect(status().isNoContent());

        AdminResetPasswordRequest confirm = new AdminResetPasswordRequest(token, "newPassw0rd!");
        mockMvc.perform(post("/auth/admin/password-reset/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(confirm)))
                .andExpect(status().isNoContent());

        var admin = administratorRepository.findById("admin-001").orElseThrow();
        assertThat(passwordEncoder.matches("newPassw0rd!", admin.getPasswordHash())).isTrue();

        mockMvc.perform(get("/auth/admin/password-reset/validate").param("token", token))
                .andExpect(status().isBadRequest());
    }
}

