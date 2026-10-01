package com.scentiva.modules.observability.controller;

import com.scentiva.modules.observability.model.AuditLog;
import com.scentiva.modules.observability.repository.AuditLogRepository;
import org.junit.jupiter.api.BeforeEach;
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
class ObservabilityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @BeforeEach
    void setUp() {
        auditLogRepository.save(AuditLog.builder()
                .userEmail("admin@scentiva.luxury")
                .action("PRICE_UPDATE")
                .entityName("ProductVariant")
                .entityId("10")
                .ipAddress("127.0.0.1")
                .details("Updated price to 32000.00 INR")
                .build());
    }

    @Test
    @DisplayName("GET /api/v1/observability/metrics as ADMIN should return JVM telemetry")
    @WithMockUser(username = "admin@scentiva.luxury", roles = {"ADMIN"})
    void shouldGetMetricsAsAdmin() throws Exception {
        mockMvc.perform(get("/api/v1/observability/metrics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.jvmVersion").exists())
                .andExpect(jsonPath("$.data.status", is("HEALTHY_OPTIMAL")));
    }

    @Test
    @DisplayName("GET /api/v1/observability/audit-logs as ADMIN should return paginated audit trail")
    @WithMockUser(username = "admin@scentiva.luxury", roles = {"ADMIN"})
    void shouldGetAuditLogsAsAdmin() throws Exception {
        mockMvc.perform(get("/api/v1/observability/audit-logs")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.totalElements").exists());
    }

    @Test
    @DisplayName("GET /api/v1/observability/metrics without auth should be rejected")
    void shouldRejectAnonymousAccessToMetrics() throws Exception {
        mockMvc.perform(get("/api/v1/observability/metrics"))
                .andExpect(status().isUnauthorized());
    }
}
