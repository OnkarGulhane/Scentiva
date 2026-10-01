package com.scentiva.modules.checkout.dto;

import com.scentiva.modules.customer.dto.AddressResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutSummaryResponse {
    private List<CheckoutItemSummary> items;
    private AddressResponse shippingAddress;
    private int totalItems;
    private BigDecimal subtotal;
    private String couponCode;
    private boolean isCouponApplied;
    private BigDecimal discountAmount;
    private BigDecimal deliveryFee;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    private boolean isFreeDelivery;
}
