package com.scentiva.modules.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminSettingsResponse {
    private String storeName;
    private String supportEmail;
    private String contactPhone;
    private BigDecimal freeShippingThreshold;
    private BigDecimal standardDeliveryFee;
    private BigDecimal taxRatePercentage;
    private String defaultCurrency;
    private int lowStockAlertThreshold;
    private boolean maintenanceMode;
}
