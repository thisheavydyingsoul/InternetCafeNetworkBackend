package com.internetcafe.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.internetcafe.dto.request.office.OfficeCreateRequest;
import com.internetcafe.dto.request.office.OfficeUpdateRequest;
import com.internetcafe.support.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
class OfficeControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @Test
    void listOffices_includesSeededOffice() throws Exception {
        mockMvc.perform(get("/offices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].id", hasItem("office-001")));
    }

    @Test
    void getOfficeById_returnsSeed() throws Exception {
        mockMvc.perform(get("/offices/office-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("office-001"))
                .andExpect(jsonPath("$.address").isNotEmpty());
    }

    @Test
    void createAndUpdateOffice() throws Exception {
        OfficeCreateRequest create = OfficeCreateRequest.builder()
                .address("Integration Test Address").
                build();

        String body = mockMvc.perform(post("/offices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(create)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.address").value("Integration Test Address"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String id = objectMapper.readTree(body).get("id").asText();

        OfficeUpdateRequest update = OfficeUpdateRequest.builder()
                .address("Updated Address")
                .build();

        mockMvc.perform(patch("/offices/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.address").value("Updated Address"));
    }
}