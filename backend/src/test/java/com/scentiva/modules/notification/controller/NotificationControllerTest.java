package com.scentiva.modules.notification.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scentiva.modules.auth.model.Role;
import com.scentiva.modules.auth.model.User;
import com.scentiva.modules.auth.repository.UserRepository;
import com.scentiva.modules.customer.model.Customer;
import com.scentiva.modules.customer.repository.CustomerRepository;
import com.scentiva.modules.notification.dto.NotificationSendRequest;
import com.scentiva.modules.notification.model.Notification;
import com.scentiva.modules.notification.model.NotificationChannel;
import com.scentiva.modules.notification.model.NotificationStatus;
import com.scentiva.modules.notification.repository.NotificationRepository;
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

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    private static final String USERNAME = "notif.ctrl@scentiva.luxury";
    private Customer customer;
    private Notification notification;

    @BeforeEach
    void setUp() {
        notificationRepository.deleteAll();

        User user = userRepository.findByEmailAndIsDeletedFalse(USERNAME)
                .orElseGet(() -> userRepository.save(User.builder()
                        .email(USERNAME)
                        .passwordHash("hashed")
                        .role(Role.ROLE_CUSTOMER)
                        .build()));

        customer = customerRepository.findByUserEmail(USERNAME)
                .orElseGet(() -> customerRepository.save(Customer.builder()
                        .user(user)
                        .firstName("Grace")
                        .lastName("Kelly")
                        .build()));

        notification = notificationRepository.save(Notification.builder()
                .customer(customer)
                .channel(NotificationChannel.EMAIL)
                .subject("Welcome to Haute Parfumerie")
                .messageBody("Discover your signature fragrance.")
                .status(NotificationStatus.SENT)
                .sentAt(LocalDateTime.now())
                .isRead(false)
                .build());
    }

    @Test
    @DisplayName("GET /api/v1/notifications should return paginated customer notifications")
    @WithMockUser(username = USERNAME, roles = "CUSTOMER")
    void shouldGetCustomerNotifications() throws Exception {
        mockMvc.perform(get("/api/v1/notifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items[0].subject", is("Welcome to Haute Parfumerie")));
    }

    @Test
    @DisplayName("GET /api/v1/notifications/unread-count should return unread alerts count")
    @WithMockUser(username = USERNAME, roles = "CUSTOMER")
    void shouldGetUnreadCount() throws Exception {
        mockMvc.perform(get("/api/v1/notifications/unread-count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", is(1)));
    }

    @Test
    @DisplayName("PUT /api/v1/notifications/{id}/read should mark notification as read")
    @WithMockUser(username = USERNAME, roles = "CUSTOMER")
    void shouldMarkAsRead() throws Exception {
        mockMvc.perform(put("/api/v1/notifications/" + notification.getId() + "/read"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));
    }

    @Test
    @DisplayName("PUT /api/v1/notifications/read-all should mark all notifications as read")
    @WithMockUser(username = USERNAME, roles = "CUSTOMER")
    void shouldMarkAllAsRead() throws Exception {
        mockMvc.perform(put("/api/v1/notifications/read-all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));
    }

    @Test
    @DisplayName("POST /api/v1/notifications/send should dispatch notification by ADMIN")
    @WithMockUser(username = "admin@scentiva.luxury", roles = "ADMIN")
    void shouldSendNotificationByAdmin() throws Exception {
        NotificationSendRequest request = NotificationSendRequest.builder()
                .customerId(customer.getId())
                .channel(NotificationChannel.IN_APP)
                .subject("Special Private Vault Access")
                .messageBody("Your tier status grants you early access to vintage flacons.")
                .build();

        mockMvc.perform(post("/api/v1/notifications/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.subject", is("Special Private Vault Access")))
                .andExpect(jsonPath("$.data.channel", is("IN_APP")));
    }
}
