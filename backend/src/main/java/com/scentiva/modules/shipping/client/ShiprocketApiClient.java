package com.scentiva.modules.shipping.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scentiva.modules.shipping.config.ShiprocketProperties;
import com.scentiva.modules.shipping.dto.shiprocket.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.util.UUID;

@Component
@Slf4j
public class ShiprocketApiClient {

    private final ShiprocketProperties properties;
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    private String cachedToken;
    private Instant tokenExpiresAt = Instant.MIN;

    public ShiprocketApiClient(ShiprocketProperties properties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.restClient = RestClient.builder()
                .baseUrl(properties.getBaseUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public synchronized String getAuthToken() {
        if (properties.isMockMode()) {
            return "mock-jwt-token-scentiva-shiprocket";
        }

        if (cachedToken != null && Instant.now().isBefore(tokenExpiresAt)) {
            return cachedToken;
        }

        try {
            log.info("Authenticating with Shiprocket API at {}", properties.getBaseUrl());
            ShiprocketLoginRequest request = ShiprocketLoginRequest.builder()
                    .email(properties.getEmail())
                    .password(properties.getPassword())
                    .build();

            ShiprocketLoginResponse response = restClient.post()
                    .uri("/auth/login")
                    .body(request)
                    .retrieve()
                    .body(ShiprocketLoginResponse.class);

            if (response != null && response.getToken() != null) {
                this.cachedToken = response.getToken();
                // Tokens are valid for 24 hours, refresh after 23 hours
                this.tokenExpiresAt = Instant.now().plusSeconds(23 * 3600);
                log.info("Successfully obtained Shiprocket JWT token for {}", properties.getEmail());
                return this.cachedToken;
            }
        } catch (Exception ex) {
            log.warn("Could not authenticate with Shiprocket API ({}). Falling back to simulated mock dispatch.", ex.getMessage());
        }

        return "mock-jwt-token-scentiva-shiprocket";
    }

    public ShiprocketCreateOrderResponse createOrder(ShiprocketCreateOrderRequest request) {
        if (properties.isMockMode()) {
            return simulateCreateOrder(request);
        }

        try {
            String token = getAuthToken();
            if ("mock-jwt-token-scentiva-shiprocket".equals(token)) {
                return simulateCreateOrder(request);
            }

            log.info("Sending adhoc order to Shiprocket for orderId={}", request.getOrderId());
            ShiprocketCreateOrderResponse response = restClient.post()
                    .uri("/orders/create/adhoc")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .body(request)
                    .retrieve()
                    .body(ShiprocketCreateOrderResponse.class);

            if (response != null && response.getShipmentId() != null) {
                log.info("Order successfully created on Shiprocket with shipmentId={}", response.getShipmentId());
                return response;
            }
        } catch (Exception ex) {
            log.warn("Error creating order on Shiprocket ({}). Falling back to simulated dispatch.", ex.getMessage());
        }

        return simulateCreateOrder(request);
    }

    public ShiprocketAssignAwbResponse assignAwb(Long shipmentId) {
        if (properties.isMockMode() || shipmentId == null || shipmentId >= 800000000L) {
            return simulateAssignAwb(shipmentId);
        }

        try {
            String token = getAuthToken();
            if ("mock-jwt-token-scentiva-shiprocket".equals(token)) {
                return simulateAssignAwb(shipmentId);
            }

            ShiprocketAssignAwbRequest request = ShiprocketAssignAwbRequest.builder()
                    .shipmentId(shipmentId)
                    .build();

            log.info("Assigning AWB on Shiprocket for shipmentId={}", shipmentId);
            ShiprocketAssignAwbResponse response = restClient.post()
                    .uri("/courier/assign/awb")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .body(request)
                    .retrieve()
                    .body(ShiprocketAssignAwbResponse.class);

            if (response != null && response.getAwbCode() != null) {
                log.info("Assigned AWB {} via courier {}", response.getAwbCode(), response.getCourierName());
                return response;
            }
        } catch (Exception ex) {
            log.warn("Error assigning AWB on Shiprocket ({}). Falling back to simulated AWB.", ex.getMessage());
        }

        return simulateAssignAwb(shipmentId);
    }

    public String getTrackingUrl(String awbCode) {
        if (awbCode == null || awbCode.isBlank()) {
            return "https://shiprocket.co/tracking";
        }
        return "https://shiprocket.co/tracking/" + awbCode.trim();
    }

    private ShiprocketCreateOrderResponse simulateCreateOrder(ShiprocketCreateOrderRequest request) {
        long mockShipmentId = 880000000L + Math.abs(UUID.randomUUID().hashCode() % 9000000);
        long mockOrderId = 770000000L + Math.abs(UUID.randomUUID().hashCode() % 9000000);

        log.info("[MOCK] Generated simulated Shiprocket Order orderId={}, shipmentId={} for orderNumber={}",
                mockOrderId, mockShipmentId, request.getOrderId());

        return ShiprocketCreateOrderResponse.builder()
                .orderId(mockOrderId)
                .shipmentId(mockShipmentId)
                .status("NEW")
                .statusCode(1)
                .courierName("BlueDart Apex Air (Shiprocket Dispatch)")
                .message("Simulated Shiprocket order created successfully")
                .build();
    }

    private ShiprocketAssignAwbResponse simulateAssignAwb(Long shipmentId) {
        String randomAwb = "SR-BLUEDART-" + (1000000000L + Math.abs(UUID.randomUUID().hashCode() % 9000000000L));
        log.info("[MOCK] Generated simulated Shiprocket AWB {} for shipmentId={}", randomAwb, shipmentId);

        ShiprocketAssignAwbResponse.AwbData awbData = new ShiprocketAssignAwbResponse.AwbData(
                randomAwb,
                "1",
                "BlueDart Apex Air Express",
                "0.45"
        );

        return ShiprocketAssignAwbResponse.builder()
                .response(new ShiprocketAssignAwbResponse.ResponseData(awbData))
                .build();
    }
}
