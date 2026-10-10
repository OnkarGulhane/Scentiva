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
import com.scentiva.modules.shipping.dto.ShipmentResponse;
import com.scentiva.modules.shipping.dto.shiprocket.ShiprocketWebhookPayload;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ShiprocketWebhookControllerTest {

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

    private Order testOrder;
    private ShipmentResponse shipmentResponse;

    @BeforeEach
    void setUp() {
        String unique = UUID.randomUUID().toString().substring(0, 8);
        User user = userRepository.save(User.builder()
                .email("shiprocket-webhook-" + unique + "@example.com")
                .passwordHash("hashed")
                .role(Role.ROLE_CUSTOMER)
                .build());

        Customer customer = customerRepository.save(Customer.builder()
                .user(user)
                .firstName("Rohan")
                .lastName("Verma")
                .phone("9811223344")
                .build());

        testOrder = orderRepository.save(Order.builder()
                .orderNumber("SC-SR-" + unique)
                .customer(customer)
                .status(OrderStatus.CONFIRMED)
                .subtotal(new BigDecimal("15000.00"))
                .totalAmount(new BigDecimal("15000.00"))
                .build());

        ShipmentCreateRequest request = ShipmentCreateRequest.builder()
                .orderId(testOrder.getId())
                .provider(ShippingProviderType.SHIPROCKET)
                .carrierName("Delhivery via Shiprocket")
                .build();

        shipmentResponse = shippingService.createShipment(request);
    }

    @Test
    @DisplayName("Shiprocket Webhook updates milestone and delivers order")
    void testShiprocketWebhookDelivered() throws Exception {
        ShiprocketWebhookPayload payload = ShiprocketWebhookPayload.builder()
                .awb(shipmentResponse.getTrackingNumber())
                .orderId(testOrder.getOrderNumber())
                .currentStatus("DELIVERED")
                .courierName("Delhivery Express")
                .location("Worli Hub, Mumbai")
                .build();

        mockMvc.perform(post("/api/v1/shipping/webhooks/shiprocket")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("SUCCESS")));

        Order updated = orderRepository.findById(testOrder.getId()).orElseThrow();
        assertEquals(OrderStatus.DELIVERED, updated.getStatus());
    }
}
