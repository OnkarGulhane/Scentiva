package com.scentiva.modules.admin.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminSettingsUpdateRequest {
    @NotBlank(message = "Store name is required")
    private String storeName;

    @NotBlank(message = "Support email is required")
    @Email(message = "Valid email is required")
    private String supportEmail;

    private String contactPhone;

    @NotNull(message = "Free shipping threshold is required")
    private BigDecimal freeShippingThreshold;

    @NotNull(message = "Standard delivery fee is required")
    private BigDecimal standardDeliveryFee;

    @NotNull(message = "Tax rate percentage is required")
    private BigDecimal taxRatePercentage;

    @NotBlank(message = "Currency is required")
    private String defaultCurrency;

    private int lowStockAlertThreshold;

    private boolean maintenanceMode;
}
