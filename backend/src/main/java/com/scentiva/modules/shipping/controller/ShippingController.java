package com.scentiva.modules.shipping.controller;

import com.scentiva.common.response.ApiResponse;
import com.scentiva.modules.shipping.dto.*;
import com.scentiva.modules.shipping.service.ShippingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/shipping")
@RequiredArgsConstructor
@Tag(name = "Shipping & Logistics Engine", description = "Shipment tracking, courier dispatch, event logs and delivery lifecycle")
public class ShippingController {

    private final ShippingService shippingService;

    @GetMapping("/track/{trackingNumber}")
    @Operation(summary = "Track shipment", description = "Public or customer shipment tracking timeline lookup by tracking number.")
    public ResponseEntity<ApiResponse<ShipmentTrackingResponse>> trackShipment(@PathVariable String trackingNumber) {
        ShipmentTrackingResponse response = shippingService.trackShipment(trackingNumber);
        return ResponseEntity.ok(ApiResponse.ok(response, "Shipment tracking retrieved"));
    }

    @GetMapping("/order/{orderId}")
    @Operation(summary = "Get shipment by order ID", description = "Retrieves shipment details and event timeline for a specific order.")
    public ResponseEntity<ApiResponse<ShipmentResponse>> getShipmentByOrder(@PathVariable Long orderId) {
        ShipmentResponse response = shippingService.getShipmentByOrderId(orderId);
        return ResponseEntity.ok(ApiResponse.ok(response, "Shipment details retrieved"));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'WAREHOUSE_MANAGER', 'PRODUCT_MANAGER')")
    @Operation(summary = "Create shipment for order (Admin)", description = "Dispatches order with tracking number and creates initial shipment event.")
    public ResponseEntity<ApiResponse<ShipmentResponse>> createShipment(@Valid @RequestBody ShipmentCreateRequest request) {
        ShipmentResponse response = shippingService.createShipment(request);
        return ResponseEntity.ok(ApiResponse.ok(response, "Shipment created successfully"));
    }

    @PutMapping("/{shipmentId}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'WAREHOUSE_MANAGER', 'PRODUCT_MANAGER')")
    @Operation(summary = "Update shipment status (Admin)", description = "Appends status transit event (e.g., IN_TRANSIT, DELIVERED) and advances order lifecycle.")
    public ResponseEntity<ApiResponse<ShipmentResponse>> updateShipmentStatus(
            @PathVariable Long shipmentId,
            @Valid @RequestBody ShipmentStatusUpdateRequest request) {
        ShipmentResponse response = shippingService.updateShipmentStatus(shipmentId, request);
        return ResponseEntity.ok(ApiResponse.ok(response, "Shipment status updated"));
    }
}
