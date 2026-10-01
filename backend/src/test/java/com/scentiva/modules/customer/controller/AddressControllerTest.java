package com.scentiva.modules.customer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scentiva.modules.auth.model.Role;
import com.scentiva.modules.auth.model.User;
import com.scentiva.modules.auth.repository.UserRepository;
import com.scentiva.modules.customer.dto.AddressCreateRequest;
import com.scentiva.modules.customer.model.Address;
import com.scentiva.modules.customer.model.AddressType;
import com.scentiva.modules.customer.model.Customer;
import com.scentiva.modules.customer.repository.AddressRepository;
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

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AddressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AddressRepository addressRepository;

    private Customer customer;

    @BeforeEach
    void setUp() {
        User user = userRepository.findByEmailAndIsDeletedFalse("test.customer@scentiva.luxury")
                .orElseGet(() -> userRepository.save(User.builder()
                        .email("test.customer@scentiva.luxury")
                        .passwordHash("hashed")
                        .role(Role.ROLE_CUSTOMER)
                        .build()));

        customer = customerRepository.findByUserEmail("test.customer@scentiva.luxury")
                .orElseGet(() -> customerRepository.save(Customer.builder()
                        .user(user)
                        .firstName("Test")
                        .lastName("Customer")
                        .phone("+91 98765 00000")
                        .build()));

        if (addressRepository.findByCustomerIdAndIsDeletedFalse(customer.getId()).isEmpty()) {
            addressRepository.save(Address.builder()
                    .customer(customer)
                    .fullName("Test Customer")
                    .phone("+91 98765 00000")
                    .addressLine1("123 Luxury Lane")
                    .city("Pune")
                    .state("Maharashtra")
                    .postalCode("411006")
                    .isDefault(true)
                    .addressType(AddressType.HOME)
                    .build());
        }
    }

    @Test
    @DisplayName("GET /api/v1/customer/addresses should return address book for logged-in user")
    @WithMockUser(username = "test.customer@scentiva.luxury", roles = "CUSTOMER")
    void shouldGetAddresses() throws Exception {
        mockMvc.perform(get("/api/v1/customer/addresses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data[0].city", is("Pune")));
    }

    @Test
    @DisplayName("POST /api/v1/customer/addresses should create new address")
    @WithMockUser(username = "test.customer@scentiva.luxury", roles = "CUSTOMER")
    void shouldCreateAddress() throws Exception {
        AddressCreateRequest request = AddressCreateRequest.builder()
                .fullName("Test Customer Work")
                .phone("+91 98765 00000")
                .addressLine1("456 IT Park")
                .city("Hinjawadi")
                .state("Maharashtra")
                .postalCode("411057")
                .addressType(AddressType.WORK)
                .build();

        mockMvc.perform(post("/api/v1/customer/addresses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.city", is("Hinjawadi")));
    }
}
