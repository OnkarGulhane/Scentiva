package com.scentiva.modules.promotion.dto;

import com.scentiva.modules.promotion.model.DiscountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponValidationResponse {
    private String code;
    private boolean isValid;
    private DiscountType discountType;
    private BigDecimal discountValue;
    private BigDecimal calculatedDiscount;
    private String message;
}
