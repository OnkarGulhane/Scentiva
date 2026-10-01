package com.scentiva.modules.checkout.controller;

import com.scentiva.common.response.ApiResponse;
import com.scentiva.modules.checkout.dto.*;
import com.scentiva.modules.checkout.service.CheckoutService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/checkout")
@RequiredArgsConstructor
@Tag(name = "Checkout Orchestration", description = "Multi-step checkout workflow, stock hold reservation, coupon application, idempotency and payment routing")
public class CheckoutController {

    private final CheckoutService checkoutService;

    @GetMapping("/review")
    @Operation(summary = "Pre-flight checkout review", description = "Calculates cart line items, authoritative subtotals, coupon discounts, and delivery fees for an address.")
    public ResponseEntity<ApiResponse<CheckoutSummaryResponse>> reviewCheckout(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) Long shippingAddressId,
            @RequestParam(required = false) String couponCode) {
        String email = userDetails.getUsername();
        CheckoutSummaryResponse response = checkoutService.reviewCheckout(email, shippingAddressId, couponCode);
        return ResponseEntity.ok(ApiResponse.ok(response, "Checkout review calculated successfully"));
    }

    @PostMapping("/process")
    @Operation(summary = "Process and place order", description = "Executes order creation, 15-min stock hold reservation, idempotency guard, and initiates payment gateway.")
    public ResponseEntity<ApiResponse<CheckoutProcessResponse>> processCheckout(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKeyHeader,
            @RequestHeader(value = "X-Idempotency-Key", required = false) String xIdempotencyKeyHeader,
            @Valid @RequestBody CheckoutProcessRequest request) {
        String email = userDetails.getUsername();
        String idempotencyKey = idempotencyKeyHeader != null ? idempotencyKeyHeader : xIdempotencyKeyHeader;
        CheckoutProcessResponse response = checkoutService.processCheckout(email, idempotencyKey, request);
        return ResponseEntity.ok(ApiResponse.ok(response, "Order processed successfully"));
    }

    @PostMapping("/verify")
    @Operation(summary = "Verify checkout payment", description = "Verifies 3D Secure / OTP payment confirmation and marks order as CONFIRMED.")
    public ResponseEntity<ApiResponse<CheckoutVerifyResponse>> verifyCheckout(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CheckoutVerifyRequest request) {
        String email = userDetails.getUsername();
        CheckoutVerifyResponse response = checkoutService.verifyCheckout(email, request);
        return ResponseEntity.ok(ApiResponse.ok(response, "Payment verification processed"));
    }
}
