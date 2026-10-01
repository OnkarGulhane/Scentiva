package com.scentiva.modules.admin.dto;

import com.scentiva.modules.customer.model.LoyaltyTier;
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
public class AdminCustomerSummaryResponse {
    private Long customerId;
    private Long userId;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private LoyaltyTier loyaltyTier;
    private long totalOrders;
    private BigDecimal totalSpent;
    private boolean isUserActive;
    private LocalDateTime registeredAt;
}
