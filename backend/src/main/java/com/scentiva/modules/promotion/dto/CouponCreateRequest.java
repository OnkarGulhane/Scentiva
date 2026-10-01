package com.scentiva.modules.promotion.dto;

import com.scentiva.modules.promotion.model.DiscountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponCreateRequest {

    @NotBlank(message = "Coupon code is required")
    private String code;

    @NotNull(message = "Discount type is required")
    private DiscountType discountType;

    @NotNull(message = "Discount value is required")
    @Positive(message = "Discount value must be positive")
    private BigDecimal discountValue;

    @Builder.Default
    private BigDecimal minOrderValue = BigDecimal.ZERO;

    private BigDecimal maxDiscount;

    private Integer usageLimitGlobal;

    @Builder.Default
    private Integer usageLimitPerUser = 1;

    private LocalDateTime expiresAt;

    @Builder.Default
    private boolean isActive = true;
}
