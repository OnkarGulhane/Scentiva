package com.scentiva.modules.admin.service;

import com.scentiva.common.response.ApiPaginatedResponse;
import com.scentiva.modules.admin.dto.AdminCustomerDetailResponse;
import com.scentiva.modules.admin.dto.AdminCustomerSummaryResponse;
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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AdminCustomerManagementServiceTest {

    @Autowired
    private AdminCustomerManagementService adminCustomerService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;

    private Customer customer;

    @BeforeEach
    void setUp() {
        User user = userRepository.save(User.builder()
                .email("vip.client@scentiva.luxury")
                .passwordHash("hashed")
                .role(Role.ROLE_CUSTOMER)
                .status(UserStatus.ACTIVE)
                .build());

        customer = customerRepository.save(Customer.builder()
                .user(user)
                .firstName("Victoria")
                .lastName("Beckham")
                .phone("+91 99880 11223")
                .build());
    }

    @Test
    @DisplayName("Should list paginated customers with spend metrics")
    void shouldGetAllCustomers() {
        ApiPaginatedResponse<AdminCustomerSummaryResponse> response =
                adminCustomerService.getAllCustomers(PageRequest.of(0, 10));

        assertThat(response.getItems()).isNotEmpty();
        assertThat(response.getItems().stream().anyMatch(c -> c.getEmail().equals("vip.client@scentiva.luxury"))).isTrue();
    }

    @Test
    @DisplayName("Should get customer details by customer ID")
    void shouldGetCustomerDetails() {
        AdminCustomerDetailResponse details = adminCustomerService.getCustomerDetails(customer.getId());

        assertThat(details).isNotNull();
        assertThat(details.getEmail()).isEqualTo("vip.client@scentiva.luxury");
        assertThat(details.getFirstName()).isEqualTo("Victoria");
    }

    @Test
    @DisplayName("Should update customer account status to SUSPENDED")
    void shouldUpdateCustomerStatus() {
        adminCustomerService.updateCustomerStatus(customer.getId(), UserStatus.SUSPENDED);

        AdminCustomerDetailResponse updated = adminCustomerService.getCustomerDetails(customer.getId());
        assertThat(updated.isUserActive()).isFalse();
    }
}
