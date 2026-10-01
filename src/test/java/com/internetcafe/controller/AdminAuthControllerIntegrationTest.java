package com.internetcafe.controller;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.internetcafe.security.oauth.GoogleIdTokenVerifierService;
import com.internetcafe.support.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
public class AdminAuthControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired JsonMapper objectMapper;

    @MockitoBean GoogleIdTokenVerifierService googleVerifier;

    @Test
    void googleLogin_returnsTokensForSeededAdmin() throws Exception {
        GoogleIdToken.Payload payload = new GoogleIdToken.Payload();
        payload.setEmail("izranovks@gmail.com");
        payload.setEmailVerified(true);
        payload.setSubject("it-google-sub");
        when(googleVerifier.verify(anyString())).thenReturn(payload);

        String response = mockMvc.perform(post("/auth/admin/google")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"idToken\":\"fake-token\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty())
                .andExpect(jsonPath("$.admin.email").value("izranovks@gmail.com"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode node = objectMapper.readTree(response);
        String refresh = node.get("refreshToken").asText();

        mockMvc.perform(post("/auth/admin/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"refreshToken\":\"" + refresh + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty());
    }
}
