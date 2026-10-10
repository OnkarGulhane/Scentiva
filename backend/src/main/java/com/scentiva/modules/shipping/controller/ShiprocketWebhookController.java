package com.scentiva.modules.shipping.controller;

import com.scentiva.common.response.ApiResponse;
import com.scentiva.modules.order.model.Order;
import com.scentiva.modules.order.model.OrderStatus;
import com.scentiva.modules.order.repository.OrderRepository;
import com.scentiva.modules.shipping.dto.shiprocket.ShiprocketWebhookPayload;
import com.scentiva.modules.shipping.model.Shipment;
import com.scentiva.modules.shipping.model.ShipmentEvent;
import com.scentiva.modules.shipping.model.ShipmentStatus;
import com.scentiva.modules.shipping.repository.ShipmentEventRepository;
import com.scentiva.modules.shipping.repository.ShipmentRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/shipping/webhooks")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Shiprocket Webhook Receiver", description = "Asynchronous courier milestone events from Shiprocket logistics network")
public class ShiprocketWebhookController {

    private final ShipmentRepository shipmentRepository;
    private final ShipmentEventRepository shipmentEventRepository;
    private final OrderRepository orderRepository;

    @PostMapping("/shiprocket")
    @Operation(summary = "Process Shiprocket Tracking Webhook", description = "Receives live courier milestones and transitions order lifecycle")
    @Transactional
    public ResponseEntity<ApiResponse<String>> handleShiprocketWebhook(@RequestBody ShiprocketWebhookPayload payload) {
        log.info("Received Shiprocket tracking webhook: awb={}, orderId={}, status={}",
                payload.getAwb(), payload.getOrderId(), payload.getCurrentStatus());

        Optional<Shipment> shipmentOpt = Optional.empty();

        if (payload.getAwb() != null && !payload.getAwb().isBlank()) {
            shipmentOpt = shipmentRepository.findByTrackingNumberWithEvents(payload.getAwb().trim());
        }

        if (shipmentOpt.isEmpty() && payload.getOrderId() != null && !payload.getOrderId().isBlank()) {
            Optional<Order> orderOpt = orderRepository.findByOrderNumberAndIsDeletedFalse(payload.getOrderId().trim());
            if (orderOpt.isPresent()) {
                shipmentOpt = shipmentRepository.findByOrderIdWithEvents(orderOpt.get().getId());
            }
        }

        if (shipmentOpt.isEmpty()) {
            log.warn("Shipment record not found for webhook: awb={}, orderId={}", payload.getAwb(), payload.getOrderId());
            return ResponseEntity.ok(ApiResponse.ok("Event received but shipment record not found", "Ignored"));
        }

        Shipment shipment = shipmentOpt.get();
        ShipmentStatus newStatus = mapToShipmentStatus(payload.getCurrentStatus());
        shipment.setStatus(newStatus);

        if (newStatus == ShipmentStatus.DELIVERED) {
            shipment.setDeliveredAt(LocalDateTime.now());
            Order order = shipment.getOrder();
            order.setStatus(OrderStatus.DELIVERED);
            orderRepository.save(order);
            log.info("Auto-transitioned order={} to DELIVERED via Shiprocket webhook", order.getOrderNumber());
        }

        shipmentRepository.save(shipment);

        String location = payload.getLocation() != null && !payload.getLocation().isBlank()
                ? payload.getLocation()
                : (payload.getCourierName() != null ? payload.getCourierName() + " Hub" : "Transit Hub");

        String description = "Shiprocket Milestone: " +
                (payload.getCurrentStatus() != null ? payload.getCurrentStatus() : "Courier Transit Update");

        ShipmentEvent event = ShipmentEvent.builder()
                .shipment(shipment)
                .status(newStatus)
                .location(location)
                .description(description)
                .eventTimestamp(LocalDateTime.now())
                .build();

        shipment.addEvent(event);
        shipmentEventRepository.save(event);

        log.info("Saved Shiprocket shipment event id={}, status={} for tracking={}",
                event.getId(), newStatus, shipment.getTrackingNumber());

        return ResponseEntity.ok(ApiResponse.ok("Shiprocket event processed successfully: " + newStatus, "SUCCESS"));
    }

    private ShipmentStatus mapToShipmentStatus(String rawStatus) {
        if (rawStatus == null) {
            return ShipmentStatus.IN_TRANSIT;
        }

        String status = rawStatus.trim().toUpperCase();

        if (status.contains("DELIVERED")) {
            return ShipmentStatus.DELIVERED;
        }
        if (status.contains("OUT FOR DELIVERY") || status.contains("OUT_FOR_DELIVERY")) {
            return ShipmentStatus.OUT_FOR_DELIVERY;
        }
        if (status.contains("CANCEL") || status.contains("CANCELLED")) {
            return ShipmentStatus.CANCELLED;
        }
        if (status.contains("RTO") || status.contains("RETURN")) {
            return ShipmentStatus.RETURNED;
        }
        if (status.contains("PICKED UP") || status.contains("IN TRANSIT") || status.contains("DISPATCH") || status.contains("HUB")) {
            return ShipmentStatus.IN_TRANSIT;
        }

        return ShipmentStatus.IN_TRANSIT;
    }
}
