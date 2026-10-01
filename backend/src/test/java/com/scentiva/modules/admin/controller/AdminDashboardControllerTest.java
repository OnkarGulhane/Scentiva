package com.scentiva.modules.admin.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AdminDashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /api/v1/admin/dashboard/summary should return KPIs for ADMIN")
    @WithMockUser(username = "admin@scentiva.luxury", roles = "ADMIN")
    void shouldGetDashboardSummary() throws Exception {
        mockMvc.perform(get("/api/v1/admin/dashboard/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.totalRevenue").exists())
                .andExpect(jsonPath("$.data.totalOrders").exists());
    }

    @Test
    @DisplayName("GET /api/v1/admin/dashboard/sales-analytics should return trends for ADMIN")
    @WithMockUser(username = "admin@scentiva.luxury", roles = "ADMIN")
    void shouldGetSalesAnalytics() throws Exception {
        mockMvc.perform(get("/api/v1/admin/dashboard/sales-analytics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.trends").isArray());
    }

    @Test
    @DisplayName("GET /api/v1/admin/dashboard/top-products should return top products for ADMIN")
    @WithMockUser(username = "admin@scentiva.luxury", roles = "ADMIN")
    void shouldGetTopProducts() throws Exception {
        mockMvc.perform(get("/api/v1/admin/dashboard/top-products").param("limit", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("GET /api/v1/admin/dashboard/summary should deny anonymous access")
    void shouldDenyAnonymousAccess() throws Exception {
        mockMvc.perform(get("/api/v1/admin/dashboard/summary"))
                .andExpect(status().isUnauthorized());
    }
}
