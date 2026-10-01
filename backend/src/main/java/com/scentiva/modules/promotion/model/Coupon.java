package com.scentiva.modules.promotion.model;

import com.scentiva.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "coupons")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Coupon extends BaseEntity {

    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false, length = 50)
    private DiscountType discountType;

    @Column(name = "discount_value", nullable = false, precision = 12, scale = 2)
    private BigDecimal discountValue;

    @Column(name = "min_order_value", nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal minOrderValue = BigDecimal.ZERO;

    @Column(name = "max_discount", precision = 12, scale = 2)
    private BigDecimal maxDiscount;

    @Column(name = "usage_limit_global")
    private Integer usageLimitGlobal;

    @Column(name = "usage_limit_per_user")
    @Builder.Default
    private Integer usageLimitPerUser = 1;

    @Column(name = "redemption_count", nullable = false)
    @Builder.Default
    private Integer redemptionCount = 0;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean isActive = true;

    public boolean isExpired() {
        return expiresAt != null && expiresAt.isBefore(LocalDateTime.now());
    }

    public boolean isUsageLimitReached() {
        return usageLimitGlobal != null && redemptionCount >= usageLimitGlobal;
    }

    public void incrementRedemption() {
        this.redemptionCount++;
    }
}
