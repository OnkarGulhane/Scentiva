package com.scentiva.modules.promotion.service;

import com.scentiva.modules.promotion.dto.CouponValidationResponse;
import com.scentiva.modules.promotion.model.Coupon;
import com.scentiva.modules.promotion.model.DiscountType;
import com.scentiva.modules.promotion.repository.CouponRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CouponServiceTest {

    @Autowired
    private CouponService couponService;

    @Autowired
    private CouponRepository couponRepository;

    @BeforeEach
    void setUp() {
        // 1. Percentage coupon with max cap: 20% off up to ₹500, min order ₹1000
        couponRepository.findByCodeIgnoreCaseAndIsDeletedFalse("TEST_LUXURY20")
                .orElseGet(() -> couponRepository.save(Coupon.builder()
                        .code("TEST_LUXURY20")
                        .discountType(DiscountType.PERCENTAGE)
                        .discountValue(new BigDecimal("20.00"))
                        .minOrderValue(new BigDecimal("1000.00"))
                        .maxDiscount(new BigDecimal("500.00"))
                        .usageLimitGlobal(100)
                        .redemptionCount(0)
                        .isActive(true)
                        .expiresAt(LocalDateTime.now().plusDays(30))
                        .build()));

        // 2. Fixed coupon: ₹300 off, min order ₹1500
        couponRepository.findByCodeIgnoreCaseAndIsDeletedFalse("TEST_FLAT300")
                .orElseGet(() -> couponRepository.save(Coupon.builder()
                        .code("TEST_FLAT300")
                        .discountType(DiscountType.FIXED_AMOUNT)
                        .discountValue(new BigDecimal("300.00"))
                        .minOrderValue(new BigDecimal("1500.00"))
                        .usageLimitGlobal(50)
                        .redemptionCount(0)
                        .isActive(true)
                        .expiresAt(LocalDateTime.now().plusDays(30))
                        .build()));

        // 3. Expired coupon
        couponRepository.findByCodeIgnoreCaseAndIsDeletedFalse("TEST_EXPIRED50")
                .orElseGet(() -> couponRepository.save(Coupon.builder()
                        .code("TEST_EXPIRED50")
                        .discountType(DiscountType.PERCENTAGE)
                        .discountValue(new BigDecimal("50.00"))
                        .minOrderValue(new BigDecimal("500.00"))
                        .usageLimitGlobal(100)
                        .redemptionCount(0)
                        .isActive(true)
                        .expiresAt(LocalDateTime.now().minusDays(1))
                        .build()));

        // 4. Exhausted coupon
        couponRepository.findByCodeIgnoreCaseAndIsDeletedFalse("TEST_EXHAUSTED")
                .orElseGet(() -> couponRepository.save(Coupon.builder()
                        .code("TEST_EXHAUSTED")
                        .discountType(DiscountType.FIXED_AMOUNT)
                        .discountValue(new BigDecimal("100.00"))
                        .minOrderValue(new BigDecimal("500.00"))
                        .usageLimitGlobal(5)
                        .redemptionCount(5)
                        .isActive(true)
                        .expiresAt(LocalDateTime.now().plusDays(30))
                        .build()));
    }

    @Test
    @DisplayName("Should validate percentage coupon and calculate discount within cap")
    void shouldValidatePercentageCouponWithinCap() {
        // Subtotal = 2000 => 20% is 400 (cap is 500)
        CouponValidationResponse response = couponService.validateAndCalculateDiscount("TEST_LUXURY20", new BigDecimal("2000.00"));

        assertThat(response.isValid()).isTrue();
        assertThat(response.getCode()).isEqualTo("TEST_LUXURY20");
        assertThat(response.getCalculatedDiscount()).isEqualByComparingTo(new BigDecimal("400.00"));
    }

    @Test
    @DisplayName("Should clamp percentage discount to maxDiscount when exceeded")
    void shouldClampPercentageDiscountToMaxCap() {
        // Subtotal = 5000 => 20% is 1000, capped at 500
        CouponValidationResponse response = couponService.validateAndCalculateDiscount("TEST_LUXURY20", new BigDecimal("5000.00"));

        assertThat(response.isValid()).isTrue();
        assertThat(response.getCalculatedDiscount()).isEqualByComparingTo(new BigDecimal("500.00"));
    }

    @Test
    @DisplayName("Should reject coupon when subtotal does not meet minOrderValue")
    void shouldRejectWhenBelowMinOrderValue() {
        CouponValidationResponse response = couponService.validateAndCalculateDiscount("TEST_LUXURY20", new BigDecimal("800.00"));

        assertThat(response.isValid()).isFalse();
        assertThat(response.getCalculatedDiscount()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(response.getMessage()).contains("Minimum order value");
    }

    @Test
    @DisplayName("Should calculate fixed amount discount correctly")
    void shouldCalculateFixedAmountDiscount() {
        CouponValidationResponse response = couponService.validateAndCalculateDiscount("TEST_FLAT300", new BigDecimal("2000.00"));

        assertThat(response.isValid()).isTrue();
        assertThat(response.getCalculatedDiscount()).isEqualByComparingTo(new BigDecimal("300.00"));
    }

    @Test
    @DisplayName("Should reject expired coupon")
    void shouldRejectExpiredCoupon() {
        CouponValidationResponse response = couponService.validateAndCalculateDiscount("TEST_EXPIRED50", new BigDecimal("2000.00"));

        assertThat(response.isValid()).isFalse();
        assertThat(response.getMessage()).contains("expired");
    }

    @Test
    @DisplayName("Should reject exhausted coupon when usage limit reached")
    void shouldRejectExhaustedCoupon() {
        CouponValidationResponse response = couponService.validateAndCalculateDiscount("TEST_EXHAUSTED", new BigDecimal("1000.00"));

        assertThat(response.isValid()).isFalse();
        assertThat(response.getMessage()).contains("limit reached");
    }

    @Test
    @DisplayName("Should increment redemption count on redemption recording")
    void shouldRecordRedemption() {
        couponService.recordRedemption("TEST_LUXURY20");

        Coupon coupon = couponService.getCouponByCode("TEST_LUXURY20");
        assertThat(coupon.getRedemptionCount()).isEqualTo(1);
    }
}
