package com.scentiva.modules.promotion.dto;

import com.scentiva.modules.promotion.model.DiscountType;
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
public class CouponResponse {
    private Long id;
    private String code;
    private DiscountType discountType;
    private BigDecimal discountValue;
    private BigDecimal minOrderValue;
    private BigDecimal maxDiscount;
    private Integer usageLimitGlobal;
    private Integer usageLimitPerUser;
    private Integer redemptionCount;
    private LocalDateTime expiresAt;
    private boolean isActive;
    private LocalDateTime createdAt;
}
