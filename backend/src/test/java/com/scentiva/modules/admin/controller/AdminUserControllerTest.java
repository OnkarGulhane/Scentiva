package com.scentiva.modules.admin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scentiva.modules.admin.dto.AdminUserRoleUpdateRequest;
import com.scentiva.modules.admin.dto.AdminUserStatusUpdateRequest;
import com.scentiva.modules.auth.model.Role;
import com.scentiva.modules.auth.model.User;
import com.scentiva.modules.auth.model.UserStatus;
import com.scentiva.modules.auth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AdminUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = userRepository.save(User.builder()
                .email("test.staff@scentiva.luxury")
                .passwordHash("hashed")
                .role(Role.ROLE_PRODUCT_MANAGER)
                .status(UserStatus.ACTIVE)
                .build());
    }

    @Test
    @DisplayName("GET /api/v1/admin/users should list all staff users for SUPER_ADMIN")
    @WithMockUser(username = "super@scentiva.luxury", roles = "SUPER_ADMIN")
    void shouldGetAllStaffUsersForSuperAdmin() throws Exception {
        mockMvc.perform(get("/api/v1/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("PUT /api/v1/admin/users/{id}/role should update user role for SUPER_ADMIN")
    @WithMockUser(username = "super@scentiva.luxury", roles = "SUPER_ADMIN")
    void shouldUpdateUserRole() throws Exception {
        AdminUserRoleUpdateRequest request = AdminUserRoleUpdateRequest.builder()
                .role(Role.ROLE_MARKETING_MANAGER)
                .build();

        mockMvc.perform(put("/api/v1/admin/users/" + user.getId() + "/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.role", is("ROLE_MARKETING_MANAGER")));
    }

    @Test
    @DisplayName("PUT /api/v1/admin/users/{id}/status should update user status for SUPER_ADMIN")
    @WithMockUser(username = "super@scentiva.luxury", roles = "SUPER_ADMIN")
    void shouldUpdateUserStatus() throws Exception {
        AdminUserStatusUpdateRequest request = AdminUserStatusUpdateRequest.builder()
                .status(UserStatus.SUSPENDED)
                .build();

        mockMvc.perform(put("/api/v1/admin/users/" + user.getId() + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.status", is("SUSPENDED")));
    }
}
