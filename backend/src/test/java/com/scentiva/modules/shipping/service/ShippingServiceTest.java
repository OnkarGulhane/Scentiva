package com.scentiva.modules.shipping.service;

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
import com.scentiva.modules.shipping.dto.ShipmentStatusUpdateRequest;
import com.scentiva.modules.shipping.dto.ShipmentTrackingResponse;
import com.scentiva.modules.shipping.model.ShipmentStatus;
import com.scentiva.modules.shipping.model.ShippingProviderType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ShippingServiceTest {

    @Autowired
    private ShippingService shippingService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private OrderRepository orderRepository;

    private Order order;

    @BeforeEach
    void setUp() {
        User user = userRepository.findByEmailAndIsDeletedFalse("shipping.test@scentiva.luxury")
                .orElseGet(() -> userRepository.save(User.builder()
                        .email("shipping.test@scentiva.luxury")
                        .passwordHash("hashed")
                        .role(Role.ROLE_CUSTOMER)
                        .build()));

        Customer customer = customerRepository.findByUserEmail("shipping.test@scentiva.luxury")
                .orElseGet(() -> customerRepository.save(Customer.builder()
                        .user(user)
                        .firstName("Shipping")
                        .lastName("Tester")
                        .build()));

        order = orderRepository.save(Order.builder()
                .orderNumber("SC-2026-SHIP-01")
                .customer(customer)
                .status(OrderStatus.CONFIRMED)
                .subtotal(new BigDecimal("4500.00"))
                .discountAmount(BigDecimal.ZERO)
                .deliveryFee(BigDecimal.ZERO)
                .taxAmount(BigDecimal.ZERO)
                .totalAmount(new BigDecimal("4500.00"))
                .build());
    }

    @Test
    @DisplayName("Should create shipment, dispatch order, and create initial timeline event")
    void shouldCreateShipment() {
        ShipmentCreateRequest request = ShipmentCreateRequest.builder()
                .orderId(order.getId())
                .provider(ShippingProviderType.MANUAL)
                .carrierName("BlueDart Luxury Air")
                .customTrackingNumber("BD-LUX-998877")
                .dispatchLocation("Mumbai Fulfilment Atelier")
                .build();

        ShipmentResponse response = shippingService.createShipment(request);

        assertThat(response).isNotNull();
        assertThat(response.getOrderId()).isEqualTo(order.getId());
        assertThat(response.getTrackingNumber()).isEqualTo("BD-LUX-998877");
        assertThat(response.getStatus()).isEqualTo(ShipmentStatus.DISPATCHED);
        assertThat(response.getEvents()).hasSize(1);
        assertThat(response.getEvents().get(0).getLocation()).isEqualTo("Mumbai Fulfilment Atelier");

        // Verify order status progressed to SHIPPED
        Order updatedOrder = orderRepository.findById(order.getId()).orElseThrow();
        assertThat(updatedOrder.getStatus()).isEqualTo(OrderStatus.SHIPPED);
    }

    @Test
    @DisplayName("Should update shipment status to DELIVERED and complete order lifecycle")
    void shouldUpdateShipmentStatusToDelivered() {
        ShipmentCreateRequest createRequest = ShipmentCreateRequest.builder()
                .orderId(order.getId())
                .carrierName("DHL Express")
                .customTrackingNumber("DHL-123456")
                .build();

        ShipmentResponse initialShipment = shippingService.createShipment(createRequest);

        ShipmentStatusUpdateRequest updateRequest = ShipmentStatusUpdateRequest.builder()
                .status(ShipmentStatus.DELIVERED)
                .location("Customer Residence, Mumbai")
                .description("Handed over to customer with luxury gift package")
                .build();

        ShipmentResponse updatedShipment = shippingService.updateShipmentStatus(initialShipment.getId(), updateRequest);

        assertThat(updatedShipment.getStatus()).isEqualTo(ShipmentStatus.DELIVERED);
        assertThat(updatedShipment.getDeliveredAt()).isNotNull();
        assertThat(updatedShipment.getEvents()).hasSize(2);

        // Verify order status marked DELIVERED
        Order completedOrder = orderRepository.findById(order.getId()).orElseThrow();
        assertThat(completedOrder.getStatus()).isEqualTo(OrderStatus.DELIVERED);
    }

    @Test
    @DisplayName("Should track shipment timeline by tracking number")
    void shouldTrackShipment() {
        ShipmentCreateRequest createRequest = ShipmentCreateRequest.builder()
                .orderId(order.getId())
                .carrierName("FedEx Luxury")
                .customTrackingNumber("FDX-TRACK-001")
                .build();

        shippingService.createShipment(createRequest);

        ShipmentTrackingResponse tracking = shippingService.trackShipment("FDX-TRACK-001");

        assertThat(tracking.getOrderNumber()).isEqualTo("SC-2026-SHIP-01");
        assertThat(tracking.getTrackingNumber()).isEqualTo("FDX-TRACK-001");
        assertThat(tracking.getCurrentStatus()).isEqualTo(ShipmentStatus.DISPATCHED);
        assertThat(tracking.getTimeline()).isNotEmpty();
    }
}
