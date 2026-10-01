package com.internetcafe.controller;

import com.internetcafe.support.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
public class DeviceControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired MockMvc mockMvc;

    @Test
    void getAllDevices_includesSeedDevice() throws Exception {
        mockMvc.perform(get("/devices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].id", hasItem("device-001")));
    }

    @Test
    void getDeviceById_returnsDetails() throws Exception {
        mockMvc.perform(get("/devices/device-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("device-001"))
                .andExpect(jsonPath("$.deviceTypeName").value("PlayStation 5"));
    }

    @Test
    void getAvailableByOffice() throws Exception {
        mockMvc.perform(get("/devices/office/office-001/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].id", hasItem("device-001")));
    }
}
