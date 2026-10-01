package com.scentiva.modules.admin.dto;

import com.scentiva.modules.customer.dto.AddressResponse;
import com.scentiva.modules.customer.model.LoyaltyTier;
import com.scentiva.modules.order.dto.OrderSummaryResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminCustomerDetailResponse {
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
    private List<AddressResponse> addresses;
    private List<OrderSummaryResponse> recentOrders;
    private LocalDateTime registeredAt;
}
