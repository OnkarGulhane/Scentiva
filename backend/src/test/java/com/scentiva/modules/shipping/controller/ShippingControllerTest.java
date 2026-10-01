package com.scentiva.modules.shipping.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scentiva.modules.auth.model.Role;
import com.scentiva.modules.auth.model.User;
import com.scentiva.modules.auth.repository.UserRepository;
import com.scentiva.modules.customer.model.Customer;
import com.scentiva.modules.customer.repository.CustomerRepository;
import com.scentiva.modules.order.model.Order;
import com.scentiva.modules.order.model.OrderStatus;
import com.scentiva.modules.order.repository.OrderRepository;
import com.scentiva.modules.shipping.dto.ShipmentCreateRequest;
import com.scentiva.modules.shipping.dto.ShipmentStatusUpdateRequest;
import com.scentiva.modules.shipping.model.ShipmentStatus;
import com.scentiva.modules.shipping.model.ShippingProviderType;
import com.scentiva.modules.shipping.service.ShippingService;
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

import java.math.BigDecimal;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ShippingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ShippingService shippingService;

    private Order order;

    @BeforeEach
    void setUp() {
        User user = userRepository.findByEmailAndIsDeletedFalse("shipping.api@scentiva.luxury")
                .orElseGet(() -> userRepository.save(User.builder()
                        .email("shipping.api@scentiva.luxury")
                        .passwordHash("hashed")
                        .role(Role.ROLE_CUSTOMER)
                        .build()));

        Customer customer = customerRepository.findByUserEmail("shipping.api@scentiva.luxury")
                .orElseGet(() -> customerRepository.save(Customer.builder()
                        .user(user)
                        .firstName("ShipAPI")
                        .lastName("Tester")
                        .build()));

        order = orderRepository.save(Order.builder()
                .orderNumber("SC-2026-SHIP-API")
                .customer(customer)
                .status(OrderStatus.CONFIRMED)
                .subtotal(new BigDecimal("3200.00"))
                .discountAmount(BigDecimal.ZERO)
                .deliveryFee(BigDecimal.ZERO)
                .taxAmount(BigDecimal.ZERO)
                .totalAmount(new BigDecimal("3200.00"))
                .build());
    }

    @Test
    @DisplayName("POST /api/v1/shipping (Admin) should create and dispatch shipment")
    @WithMockUser(username = "admin@scentiva.luxury", roles = "ADMIN")
    void shouldCreateShipmentByAdmin() throws Exception {
        ShipmentCreateRequest request = ShipmentCreateRequest.builder()
                .orderId(order.getId())
                .provider(ShippingProviderType.MANUAL)
                .carrierName("BlueDart Air Luxury")
                .customTrackingNumber("BD-API-112233")
                .dispatchLocation("Delhi Dispatch Hub")
                .build();

        mockMvc.perform(post("/api/v1/shipping")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.trackingNumber", is("BD-API-112233")))
                .andExpect(jsonPath("$.data.status", is("DISPATCHED")));
    }

    @Test
    @DisplayName("GET /api/v1/shipping/track/{trackingNumber} should return public tracking timeline")
    void shouldGetPublicTrackingTimeline() throws Exception {
        ShipmentCreateRequest request = ShipmentCreateRequest.builder()
                .orderId(order.getId())
                .carrierName("Shiprocket Express")
                .customTrackingNumber("SR-PUB-8899")
                .build();

        shippingService.createShipment(request);

        mockMvc.perform(get("/api/v1/shipping/track/SR-PUB-8899"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.trackingNumber", is("SR-PUB-8899")))
                .andExpect(jsonPath("$.data.currentStatus", is("DISPATCHED")));
    }
}
