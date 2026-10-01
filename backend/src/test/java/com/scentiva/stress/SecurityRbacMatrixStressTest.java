package com.scentiva.stress;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityRbacMatrixStressTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("RBAC Matrix: Public storefront endpoints must allow unauthenticated visitors")
    void shouldAllowPublicAccessToStorefront() throws Exception {
        mockMvc.perform(get("/api/v1/products")).andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/brands")).andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/categories")).andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/stories")).andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/banners")).andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/seo/sitemap")).andExpect(status().isOk());
        mockMvc.perform(get("/api/health")).andExpect(status().isOk());
    }

    @Test
    @DisplayName("RBAC Matrix: Anonymous users accessing protected backoffice endpoints must be rejected (401)")
    void shouldRejectAnonymousAccessToProtectedEndpoints() throws Exception {
        mockMvc.perform(get("/api/v1/admin/dashboard/summary")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/v1/admin/customers")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/v1/admin/users")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/v1/observability/metrics")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/v1/observability/audit-logs")).andExpect(status().isUnauthorized());
        mockMvc.perform(post("/api/v1/stories").contentType(MediaType.APPLICATION_JSON).content("{}")).andExpect(status().isUnauthorized());
        mockMvc.perform(post("/api/v1/banners").contentType(MediaType.APPLICATION_JSON).content("{}")).andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("RBAC Matrix: ROLE_CUSTOMER accessing administrator console must be forbidden (403)")
    @WithMockUser(username = "shopper@scentiva.luxury", roles = {"CUSTOMER"})
    void shouldDenyCustomerAccessToAdminEndpoints() throws Exception {
        mockMvc.perform(get("/api/v1/admin/dashboard/summary")).andExpect(status().isForbidden());
        mockMvc.perform(get("/api/v1/admin/customers")).andExpect(status().isForbidden());
        mockMvc.perform(get("/api/v1/admin/users")).andExpect(status().isForbidden());
        mockMvc.perform(get("/api/v1/observability/metrics")).andExpect(status().isForbidden());
        mockMvc.perform(get("/api/v1/observability/audit-logs")).andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("RBAC Matrix: ROLE_ADMIN must have full access to management and observability")
    @WithMockUser(username = "admin@scentiva.luxury", roles = {"ADMIN"})
    void shouldAllowAdminFullAccess() throws Exception {
        mockMvc.perform(get("/api/v1/admin/dashboard/summary")).andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/admin/customers")).andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/admin/users")).andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/observability/metrics")).andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/observability/audit-logs")).andExpect(status().isOk());
    }
}
