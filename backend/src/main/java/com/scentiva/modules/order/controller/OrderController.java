package com.scentiva.modules.order.controller;

import com.scentiva.common.response.ApiPaginatedResponse;
import com.scentiva.common.response.ApiResponse;
import com.scentiva.modules.order.dto.*;
import com.scentiva.modules.order.model.OrderStatus;
import com.scentiva.modules.order.service.OrderService;
import com.scentiva.modules.shipping.dto.ShipmentTrackingResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "Order Lifecycle & History", description = "Order retrieval, history pagination, state transitions, cancellation and tracking")
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    @Operation(summary = "Get customer order history", description = "Retrieves paginated list of orders placed by authenticated customer.")
    public ResponseEntity<ApiPaginatedResponse<OrderSummaryResponse>> getCustomerOrders(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        String email = userDetails.getUsername();
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        ApiPaginatedResponse<OrderSummaryResponse> response = orderService.getCustomerOrders(email, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{orderNumber}")
    @Operation(summary = "Get order details", description = "Retrieves detailed order with line items, snapshots, payment status, and shipment info.")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderByNumber(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String orderNumber) {
        String email = userDetails.getUsername();
        OrderResponse response = orderService.getOrderByNumber(email, orderNumber);
        return ResponseEntity.ok(ApiResponse.ok(response, "Order retrieved successfully"));
    }

    @PostMapping("/{orderNumber}/cancel")
    @Operation(summary = "Cancel order", description = "Cancels an order in PLACED/CONFIRMED state, restocks inventory and initiates payment refund.")
    public ResponseEntity<ApiResponse<OrderResponse>> cancelOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String orderNumber,
            @Valid @RequestBody OrderCancelRequest request) {
        String email = userDetails.getUsername();
        OrderResponse response = orderService.cancelOrder(email, orderNumber, request);
        return ResponseEntity.ok(ApiResponse.ok(response, "Order cancelled successfully"));
    }

    @GetMapping("/{orderNumber}/track")
    @Operation(summary = "Track order shipment", description = "Retrieves courier tracking timeline and event history for the order.")
    public ResponseEntity<ApiResponse<ShipmentTrackingResponse>> trackOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String orderNumber) {
        String email = userDetails.getUsername();
        ShipmentTrackingResponse response = orderService.trackOrder(email, orderNumber);
        return ResponseEntity.ok(ApiResponse.ok(response, "Order tracking retrieved"));
    }

    @PutMapping("/{orderNumber}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'PRODUCT_MANAGER', 'WAREHOUSE_MANAGER')")
    @Operation(summary = "Update order status (Admin)", description = "Advances order state machine (PROCESSING, SHIPPED, DELIVERED, CANCELLED).")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(
            @PathVariable String orderNumber,
            @Valid @RequestBody OrderStatusUpdateRequest request) {
        OrderResponse response = orderService.updateOrderStatus(orderNumber, request);
        return ResponseEntity.ok(ApiResponse.ok(response, "Order status updated successfully"));
    }

    @GetMapping("/admin/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'PRODUCT_MANAGER', 'WAREHOUSE_MANAGER')")
    @Operation(summary = "Get all orders (Admin)", description = "Retrieves paginated orders across all customers with optional status filter.")
    public ResponseEntity<ApiPaginatedResponse<OrderSummaryResponse>> getAllOrders(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        ApiPaginatedResponse<OrderSummaryResponse> response = orderService.getAllOrders(status, pageable);
        return ResponseEntity.ok(response);
    }
}
