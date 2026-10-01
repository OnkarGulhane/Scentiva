package com.scentiva.modules.admin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scentiva.modules.admin.dto.AdminSettingsUpdateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminSettingsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /api/v1/admin/settings should return settings for ADMIN")
    @WithMockUser(username = "admin@scentiva.luxury", roles = "ADMIN")
    void shouldGetSettings() throws Exception {
        mockMvc.perform(get("/api/v1/admin/settings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.storeName").exists());
    }

    @Test
    @DisplayName("PUT /api/v1/admin/settings should update settings for ADMIN")
    @WithMockUser(username = "admin@scentiva.luxury", roles = "ADMIN")
    void shouldUpdateSettings() throws Exception {
        AdminSettingsUpdateRequest request = AdminSettingsUpdateRequest.builder()
                .storeName("SCENTIVA Maison")
                .supportEmail("contact@scentiva.luxury")
                .freeShippingThreshold(new BigDecimal("2500.00"))
                .standardDeliveryFee(new BigDecimal("150.00"))
                .taxRatePercentage(new BigDecimal("18.00"))
                .defaultCurrency("INR")
                .lowStockAlertThreshold(5)
                .maintenanceMode(false)
                .build();

        mockMvc.perform(put("/api/v1/admin/settings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.storeName", is("SCENTIVA Maison")));
    }
}
