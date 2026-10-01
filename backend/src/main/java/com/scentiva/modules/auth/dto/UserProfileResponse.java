package com.scentiva.modules.auth.dto;

import com.scentiva.modules.auth.model.Role;
import com.scentiva.modules.auth.model.UserStatus;
import com.scentiva.modules.customer.model.LoyaltyTier;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Detailed User & Customer Profile Response DTO.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User Profile Response")
public class UserProfileResponse {

    @Schema(description = "Security user ID", example = "101")
    private Long userId;

    @Schema(description = "Commerce customer ID", example = "201")
    private Long customerId;

    @Schema(description = "User email address", example = "aria.deshmukh@scentiva.luxury")
    private String email;

    @Schema(description = "Customer first name", example = "Aria")
    private String firstName;

    @Schema(description = "Customer last name", example = "Deshmukh")
    private String lastName;

    @Schema(description = "Customer full display name", example = "Aria Deshmukh")
    private String fullName;

    @Schema(description = "Contact phone number", example = "+919876543210")
    private String phone;

    @Schema(description = "Role assigned to security account", example = "ROLE_CUSTOMER")
    private Role role;

    @Schema(description = "Account status", example = "ACTIVE")
    private UserStatus status;

    @Schema(description = "Customer loyalty tier", example = "VIP_CONNOISSEUR")
    private LoyaltyTier loyaltyTier;

    @Schema(description = "Account creation timestamp")
    private LocalDateTime memberSince;
}
