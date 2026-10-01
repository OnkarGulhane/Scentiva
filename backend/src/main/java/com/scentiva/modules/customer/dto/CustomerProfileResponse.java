package com.scentiva.modules.customer.dto;

import com.scentiva.modules.customer.model.LoyaltyTier;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerProfileResponse {
    private Long id;
    private Long userId;
    private String email;
    private String firstName;
    private String lastName;
    private String fullName;
    private String phone;
    private LoyaltyTier loyaltyTier;
    private List<AddressResponse> addresses;
    private LocalDateTime createdAt;
}
