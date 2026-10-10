package com.scentiva.modules.shipping.service.impl;

import com.scentiva.common.exception.ResourceNotFoundException;
import com.scentiva.modules.order.model.Order;
import com.scentiva.modules.order.model.OrderStatus;
import com.scentiva.modules.order.repository.OrderRepository;
import com.scentiva.modules.shipping.dto.*;
import com.scentiva.modules.shipping.model.Shipment;
import com.scentiva.modules.shipping.model.ShipmentEvent;
import com.scentiva.modules.shipping.model.ShipmentStatus;
import com.scentiva.modules.shipping.model.ShippingProviderType;
import com.scentiva.modules.shipping.provider.ShipmentCreateCommand;
import com.scentiva.modules.shipping.provider.ShipmentCreateResult;
import com.scentiva.modules.shipping.provider.ShippingProvider;
import com.scentiva.modules.shipping.repository.ShipmentEventRepository;
import com.scentiva.modules.shipping.repository.ShipmentRepository;
import com.scentiva.modules.shipping.service.ShippingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShippingServiceImpl implements ShippingService {

    private final ShipmentRepository shipmentRepository;
    private final ShipmentEventRepository shipmentEventRepository;
    private final OrderRepository orderRepository;
    private final List<ShippingProvider> shippingProviders;
    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    @Override
    @Transactional
    public ShipmentResponse createShipment(ShipmentCreateRequest request) {
        Order order = orderRepository.findById(request.getOrderId())
                .filter(o -> !o.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", request.getOrderId()));

        // Check if shipment already exists
        if (shipmentRepository.findByOrderIdAndIsDeletedFalse(order.getId()).isPresent()) {
            throw new IllegalArgumentException("Shipment already exists for order: " + order.getOrderNumber());
        }

        ShippingProviderType providerType = request.getProvider() != null
                ? request.getProvider()
                : ShippingProviderType.MANUAL;

        ShippingProvider provider = shippingProviders.stream()
                .filter(p -> p.getProviderType() == providerType)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported shipping provider: " + providerType));

        // Extract customer and recipient details
        String recipientName = order.getCustomer() != null ? order.getCustomer().getFullName() : "Valued Customer";
        String recipientPhone = order.getCustomer() != null && order.getCustomer().getPhone() != null ? order.getCustomer().getPhone() : "9820012345";
        String recipientEmail = order.getCustomer() != null && order.getCustomer().getUser() != null ? order.getCustomer().getUser().getEmail() : "concierge@scentiva.luxury";
        String addressLine = "Boutique Fragrance Vault, Baner Road";
        String city = "Pune";
        String state = "Maharashtra";
        String postalCode = "411045";
        String country = "India";

        if (order.getSnapshot() != null && order.getSnapshot().getShippingAddressSnapshotJson() != null) {
            try {
                com.fasterxml.jackson.databind.JsonNode addrNode = objectMapper.readTree(order.getSnapshot().getShippingAddressSnapshotJson());
                if (addrNode.hasNonNull("fullName")) recipientName = addrNode.get("fullName").asText();
                if (addrNode.hasNonNull("addressLine1")) addressLine = addrNode.get("addressLine1").asText();
                if (addrNode.hasNonNull("city")) city = addrNode.get("city").asText();
                if (addrNode.hasNonNull("state")) state = addrNode.get("state").asText();
                if (addrNode.hasNonNull("postalCode")) postalCode = addrNode.get("postalCode").asText();
                if (addrNode.hasNonNull("country")) country = addrNode.get("country").asText();
            } catch (Exception e) {
                log.warn("Could not parse shipping address snapshot for order={}", order.getOrderNumber());
            }
        }

        List<ShipmentItemDto> itemDtos = new java.util.ArrayList<>();
        if (order.getItems() != null) {
            for (com.scentiva.modules.order.model.OrderItem oi : order.getItems()) {
                itemDtos.add(ShipmentItemDto.builder()
                        .name(oi.getProductName() != null ? oi.getProductName() : "Luxury Fragrance")
                        .sku(oi.getSku() != null ? oi.getSku() : "SC-VAULT-EDP")
                        .quantity(oi.getQuantity())
                        .price(oi.getUnitPrice())
                        .build());
            }
        }

        ShipmentCreateCommand command = ShipmentCreateCommand.builder()
                .orderId(order.getId())
                .orderNumber(order.getOrderNumber())
                .recipientName(recipientName)
                .recipientPhone(recipientPhone)
                .recipientEmail(recipientEmail)
                .shippingAddress(addressLine)
                .city(city)
                .state(state)
                .postalCode(postalCode)
                .country(country)
                .orderTotal(order.getTotalAmount())
                .items(itemDtos)
                .carrierName(request.getCarrierName())
                .customTrackingNumber(request.getCustomTrackingNumber())
                .dispatchLocation(request.getDispatchLocation() != null ? request.getDispatchLocation() : "Mumbai Central Hub")
                .build();

        ShipmentCreateResult result = provider.createShipment(command);

        Shipment shipment = Shipment.builder()
                .order(order)
                .provider(providerType)
                .carrierName(result.getCarrierName())
                .trackingNumber(result.getTrackingNumber())
                .status(result.getStatus())
                .dispatchedAt(LocalDateTime.now())
                .build();

        shipment = shipmentRepository.save(shipment);

        // Initial Shipment Event
        ShipmentEvent event = ShipmentEvent.builder()
                .shipment(shipment)
                .status(result.getStatus())
                .location(command.getDispatchLocation())
                .description("Shipment initialized and handed over to " + result.getCarrierName())
                .eventTimestamp(LocalDateTime.now())
                .build();

        shipment.addEvent(event);
        shipmentEventRepository.save(event);

        // Advance Order Status to SHIPPED
        order.setStatus(OrderStatus.SHIPPED);
        orderRepository.save(order);

        log.info("Created shipment id={}, tracking={} for order={}",
                shipment.getId(), shipment.getTrackingNumber(), order.getOrderNumber());

        return mapToShipmentResponse(shipment);
    }

    @Override
    @Transactional
    public ShipmentResponse updateShipmentStatus(Long shipmentId, ShipmentStatusUpdateRequest request) {
        Shipment shipment = shipmentRepository.findById(shipmentId)
                .filter(s -> !s.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Shipment", "id", shipmentId));

        shipment.setStatus(request.getStatus());

        if (request.getStatus() == ShipmentStatus.DELIVERED) {
            shipment.setDeliveredAt(LocalDateTime.now());
            Order order = shipment.getOrder();
            order.setStatus(OrderStatus.DELIVERED);
            orderRepository.save(order);
            log.info("Marked order={} as DELIVERED upon shipment delivery", order.getOrderNumber());
        }

        shipment = shipmentRepository.save(shipment);

        ShipmentEvent event = ShipmentEvent.builder()
                .shipment(shipment)
                .status(request.getStatus())
                .location(request.getLocation() != null ? request.getLocation() : "Transit Facility")
                .description(request.getDescription() != null ? request.getDescription() : "Status updated to " + request.getStatus())
                .eventTimestamp(LocalDateTime.now())
                .build();

        shipment.addEvent(event);
        shipmentEventRepository.save(event);

        return mapToShipmentResponse(shipment);
    }

    @Override
    @Transactional(readOnly = true)
    public ShipmentResponse getShipmentByOrderId(Long orderId) {
        Shipment shipment = shipmentRepository.findByOrderIdWithEvents(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment", "orderId", orderId));
        return mapToShipmentResponse(shipment);
    }

    @Override
    @Transactional(readOnly = true)
    public ShipmentResponse getShipmentByTrackingNumber(String trackingNumber) {
        Shipment shipment = shipmentRepository.findByTrackingNumberWithEvents(trackingNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment", "trackingNumber", trackingNumber));
        return mapToShipmentResponse(shipment);
    }

    @Override
    @Transactional(readOnly = true)
    public ShipmentTrackingResponse trackShipment(String trackingNumber) {
        Shipment shipment = shipmentRepository.findByTrackingNumberWithEvents(trackingNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment", "trackingNumber", trackingNumber));

        List<ShipmentEventResponse> timeline = shipment.getEvents().stream()
                .map(this::mapToEventResponse)
                .toList();

        return ShipmentTrackingResponse.builder()
                .orderNumber(shipment.getOrder().getOrderNumber())
                .trackingNumber(shipment.getTrackingNumber())
                .carrierName(shipment.getCarrierName())
                .currentStatus(shipment.getStatus())
                .estimatedDelivery(shipment.getDispatchedAt() != null ? shipment.getDispatchedAt().plusDays(3) : null)
                .timeline(timeline)
                .build();
    }

    private ShipmentResponse mapToShipmentResponse(Shipment shipment) {
        List<ShipmentEventResponse> events = shipment.getEvents() != null
                ? shipment.getEvents().stream().map(this::mapToEventResponse).toList()
                : List.of();

        return ShipmentResponse.builder()
                .id(shipment.getId())
                .orderId(shipment.getOrder().getId())
                .orderNumber(shipment.getOrder().getOrderNumber())
                .provider(shipment.getProvider())
                .carrierName(shipment.getCarrierName())
                .trackingNumber(shipment.getTrackingNumber())
                .status(shipment.getStatus())
                .dispatchedAt(shipment.getDispatchedAt())
                .deliveredAt(shipment.getDeliveredAt())
                .events(events)
                .createdAt(shipment.getCreatedAt())
                .build();
    }

    private ShipmentEventResponse mapToEventResponse(ShipmentEvent event) {
        return ShipmentEventResponse.builder()
                .id(event.getId())
                .status(event.getStatus())
                .location(event.getLocation())
                .description(event.getDescription())
                .eventTimestamp(event.getEventTimestamp())
                .build();
    }
}
