package com.scentiva.stress;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.assertj.core.api.Assertions.assertThat;

class FinancialMathPrecisionTest {

    private static final BigDecimal FREE_SHIPPING_THRESHOLD = new BigDecimal("2000.00");
    private static final BigDecimal STANDARD_SHIPPING_FEE = new BigDecimal("150.00");

    @Test
    @DisplayName("Precision Invariant: High-volume luxury items calculate exact decimal total without IEEE 754 drift")
    void shouldCalculateExactTotalsForLargeNumbers() {
        // 1,000 flacons of Roja Haute Luxe at ₹2,85,550.75 each
        BigDecimal unitPrice = new BigDecimal("285550.75");
        int quantity = 1000;

        BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity)).setScale(2, RoundingMode.HALF_UP);
        assertThat(subtotal).isEqualByComparingTo("285550750.00");

        // Apply 15% VIP Maison discount
        BigDecimal discountPercent = new BigDecimal("15.00");
        BigDecimal calculatedDiscount = subtotal.multiply(discountPercent)
                .divide(new BigDecimal("100.00"), 2, RoundingMode.HALF_UP);

        assertThat(calculatedDiscount).isEqualByComparingTo("42832612.50");

        // Total after discount + Free shipping
        BigDecimal finalTotal = subtotal.subtract(calculatedDiscount).setScale(2, RoundingMode.HALF_UP);
        assertThat(finalTotal).isEqualByComparingTo("242718137.50");
    }

    @Test
    @DisplayName("Precision Invariant: Percentage discount with max cap must never exceed maximum limit")
    void shouldRespectDiscountMaxCap() {
        BigDecimal subtotal = new BigDecimal("85000.00");
        BigDecimal discountPercent = new BigDecimal("20.00");
        BigDecimal maxDiscountCap = new BigDecimal("5000.00");

        BigDecimal rawDiscount = subtotal.multiply(discountPercent).divide(new BigDecimal("100.00"), 2, RoundingMode.HALF_UP);
        // Raw discount would be ₹17,000.00, capped at ₹5,000.00
        BigDecimal cappedDiscount = rawDiscount.min(maxDiscountCap);

        assertThat(rawDiscount).isEqualByComparingTo("17000.00");
        assertThat(cappedDiscount).isEqualByComparingTo("5000.00");

        BigDecimal finalTotal = subtotal.subtract(cappedDiscount).setScale(2, RoundingMode.HALF_UP);
        assertThat(finalTotal).isEqualByComparingTo("80000.00");
    }

    @Test
    @DisplayName("Precision Invariant: Subtotal below ₹2,000.00 strictly incurs ₹150.00 delivery fee")
    void shouldApplyStandardDeliveryFeeUnderThreshold() {
        BigDecimal itemPrice = new BigDecimal("1850.00");
        int quantity = 1;
        BigDecimal subtotal = itemPrice.multiply(BigDecimal.valueOf(quantity)).setScale(2, RoundingMode.HALF_UP);

        boolean isFreeDelivery = subtotal.compareTo(FREE_SHIPPING_THRESHOLD) >= 0;
        BigDecimal deliveryFee = isFreeDelivery ? BigDecimal.ZERO : STANDARD_SHIPPING_FEE;

        BigDecimal total = subtotal.add(deliveryFee).setScale(2, RoundingMode.HALF_UP);

        assertThat(isFreeDelivery).isFalse();
        assertThat(deliveryFee).isEqualByComparingTo("150.00");
        assertThat(total).isEqualByComparingTo("2000.00");
    }

    @Test
    @DisplayName("Precision Invariant: Subtotal at or above ₹2,000.00 receives ₹0.00 free delivery")
    void shouldApplyFreeDeliveryAboveThreshold() {
        BigDecimal itemPrice = new BigDecimal("2000.00");
        int quantity = 1;
        BigDecimal subtotal = itemPrice.multiply(BigDecimal.valueOf(quantity)).setScale(2, RoundingMode.HALF_UP);

        boolean isFreeDelivery = subtotal.compareTo(FREE_SHIPPING_THRESHOLD) >= 0;
        BigDecimal deliveryFee = isFreeDelivery ? BigDecimal.ZERO : STANDARD_SHIPPING_FEE;

        BigDecimal total = subtotal.add(deliveryFee).setScale(2, RoundingMode.HALF_UP);

        assertThat(isFreeDelivery).isTrue();
        assertThat(deliveryFee).isEqualByComparingTo("0.00");
        assertThat(total).isEqualByComparingTo("2000.00");
    }

    @Test
    @DisplayName("Precision Invariant: Order total is bounded at zero (never negative)")
    void shouldNeverProduceNegativeTotals() {
        BigDecimal subtotal = new BigDecimal("500.00");
        BigDecimal extremeDiscount = new BigDecimal("1500.00");
        BigDecimal deliveryFee = BigDecimal.ZERO;

        BigDecimal netTotal = subtotal.subtract(extremeDiscount).add(deliveryFee);
        BigDecimal boundedTotal = netTotal.max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);

        assertThat(boundedTotal).isEqualByComparingTo("0.00");
    }
}
