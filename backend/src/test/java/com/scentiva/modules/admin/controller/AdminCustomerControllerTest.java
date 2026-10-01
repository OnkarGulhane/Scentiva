package com.scentiva.modules.admin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scentiva.modules.admin.dto.AdminUserStatusUpdateRequest;
import com.scentiva.modules.auth.model.Role;
import com.scentiva.modules.auth.model.User;
import com.scentiva.modules.auth.model.UserStatus;
import com.scentiva.modules.auth.repository.UserRepository;
import com.scentiva.modules.customer.model.Customer;
import com.scentiva.modules.customer.repository.CustomerRepository;
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
class AdminCustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;

    private Customer customer;

    @BeforeEach
    void setUp() {
        User user = userRepository.save(User.builder()
                .email("ctrl.cust@scentiva.luxury")
                .passwordHash("hashed")
                .role(Role.ROLE_CUSTOMER)
                .status(UserStatus.ACTIVE)
                .build());

        customer = customerRepository.save(Customer.builder()
                .user(user)
                .firstName("Coco")
                .lastName("Chanel")
                .phone("+91 91234 56789")
                .build());
    }

    @Test
    @DisplayName("GET /api/v1/admin/customers should return paginated customers for ADMIN")
    @WithMockUser(username = "admin@scentiva.luxury", roles = "ADMIN")
    void shouldGetAllCustomersForAdmin() throws Exception {
        mockMvc.perform(get("/api/v1/admin/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray());
    }

    @Test
    @DisplayName("GET /api/v1/admin/customers/{id} should return customer details for ADMIN")
    @WithMockUser(username = "admin@scentiva.luxury", roles = "ADMIN")
    void shouldGetCustomerDetailsForAdmin() throws Exception {
        mockMvc.perform(get("/api/v1/admin/customers/" + customer.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.email", is("ctrl.cust@scentiva.luxury")));
    }

    @Test
    @DisplayName("PUT /api/v1/admin/customers/{id}/status should update customer status")
    @WithMockUser(username = "admin@scentiva.luxury", roles = "ADMIN")
    void shouldUpdateCustomerStatusForAdmin() throws Exception {
        AdminUserStatusUpdateRequest request = AdminUserStatusUpdateRequest.builder()
                .status(UserStatus.SUSPENDED)
                .build();

        mockMvc.perform(put("/api/v1/admin/customers/" + customer.getId() + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));
    }
}
