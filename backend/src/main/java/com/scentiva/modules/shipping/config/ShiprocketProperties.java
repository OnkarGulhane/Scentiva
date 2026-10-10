package com.scentiva.modules.shipping.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "scentiva.shipping.shiprocket")
public class ShiprocketProperties {

    private boolean enabled = true;
    private String baseUrl = "https://apiv2.shiprocket.in/v2/console";
    private String email = "placeholder_shiprocket_email@scentiva.luxury";
    private String password = "placeholder_shiprocket_password";
    private String pickupLocation = "Primary Warehouse";
    private boolean mockMode = true;
    private double defaultWeight = 0.45;
    private int defaultLength = 15;
    private int defaultBreadth = 10;
    private int defaultHeight = 8;

    public boolean isMockMode() {
        return mockMode ||
                email == null ||
                email.isBlank() ||
                email.startsWith("placeholder") ||
                password == null ||
                password.isBlank() ||
                password.startsWith("placeholder");
    }
}
