package com.scentiva.modules.admin.service.impl;

import com.scentiva.modules.admin.dto.AdminSettingsResponse;
import com.scentiva.modules.admin.dto.AdminSettingsUpdateRequest;
import com.scentiva.modules.admin.service.AdminSettingsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicReference;

@Service
@Slf4j
public class AdminSettingsServiceImpl implements AdminSettingsService {

    private final AtomicReference<AdminSettingsResponse> currentSettings = new AtomicReference<>(
            AdminSettingsResponse.builder()
                    .storeName("SCENTIVA Haute Parfumerie")
                    .supportEmail("concierge@scentiva.luxury")
                    .contactPhone("+91 22 6900 8800")
                    .freeShippingThreshold(new BigDecimal("2000.00"))
                    .standardDeliveryFee(new BigDecimal("150.00"))
                    .taxRatePercentage(new BigDecimal("18.00"))
                    .defaultCurrency("INR")
                    .lowStockAlertThreshold(5)
                    .maintenanceMode(false)
                    .build()
    );

    @Override
    public AdminSettingsResponse getSettings() {
        return currentSettings.get();
    }

    @Override
    public AdminSettingsResponse updateSettings(AdminSettingsUpdateRequest request) {
        AdminSettingsResponse updated = AdminSettingsResponse.builder()
                .storeName(request.getStoreName())
                .supportEmail(request.getSupportEmail())
                .contactPhone(request.getContactPhone())
                .freeShippingThreshold(request.getFreeShippingThreshold())
                .standardDeliveryFee(request.getStandardDeliveryFee())
                .taxRatePercentage(request.getTaxRatePercentage())
                .defaultCurrency(request.getDefaultCurrency())
                .lowStockAlertThreshold(request.getLowStockAlertThreshold())
                .maintenanceMode(request.isMaintenanceMode())
                .build();

        currentSettings.set(updated);
        log.info("Updated global admin store settings: storeName='{}', freeShippingThreshold={}",
                updated.getStoreName(), updated.getFreeShippingThreshold());

        return updated;
    }
}
