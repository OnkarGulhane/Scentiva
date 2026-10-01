package com.scentiva.modules.admin.service;

import com.scentiva.modules.admin.dto.AdminSettingsResponse;
import com.scentiva.modules.admin.dto.AdminSettingsUpdateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class AdminSettingsServiceTest {

    @Autowired
    private AdminSettingsService adminSettingsService;

    @Test
    @DisplayName("Should retrieve default store settings")
    void shouldGetDefaultSettings() {
        AdminSettingsResponse settings = adminSettingsService.getSettings();

        assertThat(settings).isNotNull();
        assertThat(settings.getStoreName()).contains("SCENTIVA");
        assertThat(settings.getFreeShippingThreshold()).isEqualByComparingTo(new BigDecimal("2000.00"));
    }

    @Test
    @DisplayName("Should update store settings")
    void shouldUpdateSettings() {
        AdminSettingsUpdateRequest request = AdminSettingsUpdateRequest.builder()
                .storeName("SCENTIVA Private Atelier")
                .supportEmail("support@scentiva.luxury")
                .contactPhone("+91 22 9999 8888")
                .freeShippingThreshold(new BigDecimal("3000.00"))
                .standardDeliveryFee(new BigDecimal("200.00"))
                .taxRatePercentage(new BigDecimal("18.00"))
                .defaultCurrency("INR")
                .lowStockAlertThreshold(10)
                .maintenanceMode(false)
                .build();

        AdminSettingsResponse updated = adminSettingsService.updateSettings(request);

        assertThat(updated.getStoreName()).isEqualTo("SCENTIVA Private Atelier");
        assertThat(updated.getFreeShippingThreshold()).isEqualByComparingTo(new BigDecimal("3000.00"));
    }
}
