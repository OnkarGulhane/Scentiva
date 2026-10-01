package com.scentiva.modules.promotion.controller;

import com.scentiva.common.response.ApiResponse;
import com.scentiva.modules.promotion.dto.CouponCreateRequest;
import com.scentiva.modules.promotion.dto.CouponResponse;
import com.scentiva.modules.promotion.dto.CouponValidationResponse;
import com.scentiva.modules.promotion.service.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/coupons")
@RequiredArgsConstructor
@Tag(name = "Coupons & Discounts Engine", description = "Coupon validations, threshold checks, redemption tracking and admin vouchers")
public class CouponController {

    private final CouponService couponService;

    @GetMapping("/validate")
    @Operation(summary = "Validate coupon code", description = "Validates coupon against cart subtotal and calculates discount amount.")
    public ResponseEntity<ApiResponse<CouponValidationResponse>> validateCoupon(
            @RequestParam String code,
            @RequestParam(required = false, defaultValue = "0.00") BigDecimal subtotal) {
        CouponValidationResponse response = couponService.validateAndCalculateDiscount(code, subtotal);
        return ResponseEntity.ok(ApiResponse.ok(response, response.getMessage()));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'PRODUCT_MANAGER', 'MARKETING_MANAGER')")
    @Operation(summary = "Get all coupons (Admin)", description = "Retrieves all active and historical coupons.")
    public ResponseEntity<ApiResponse<List<CouponResponse>>> getAllCoupons() {
        List<CouponResponse> coupons = couponService.getAllCoupons();
        return ResponseEntity.ok(ApiResponse.ok(coupons, "Coupons retrieved successfully"));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'PRODUCT_MANAGER', 'MARKETING_MANAGER')")
    @Operation(summary = "Create coupon (Admin)", description = "Creates a new percentage or fixed discount promotional voucher.")
    public ResponseEntity<ApiResponse<CouponResponse>> createCoupon(@Valid @RequestBody CouponCreateRequest request) {
        CouponResponse coupon = couponService.createCoupon(request);
        return ResponseEntity.ok(ApiResponse.ok(coupon, "Coupon created successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'PRODUCT_MANAGER', 'MARKETING_MANAGER')")
    @Operation(summary = "Delete coupon (Admin)", description = "Soft deletes a promotional coupon.")
    public ResponseEntity<ApiResponse<Void>> deleteCoupon(@PathVariable Long id) {
        couponService.deleteCoupon(id);
        return ResponseEntity.ok(ApiResponse.ok(null, "Coupon deleted successfully"));
    }
}
