package com.scentiva.modules.shipping.provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scentiva.modules.shipping.client.ShiprocketApiClient;
import com.scentiva.modules.shipping.config.ShiprocketProperties;
import com.scentiva.modules.shipping.dto.ShipmentItemDto;
import com.scentiva.modules.shipping.model.ShipmentStatus;
import com.scentiva.modules.shipping.model.ShippingProviderType;
import com.scentiva.modules.shipping.provider.impl.ShiprocketShippingProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ShiprocketShippingProviderTest {

    private ShiprocketShippingProvider provider;

    @BeforeEach
    void setUp() {
        ShiprocketProperties properties = new ShiprocketProperties();
        properties.setMockMode(true);
        ObjectMapper objectMapper = new ObjectMapper();
        ShiprocketApiClient apiClient = new ShiprocketApiClient(properties, objectMapper);
        provider = new ShiprocketShippingProvider(apiClient, properties);
    }

    @Test
    @DisplayName("Provider type must be SHIPROCKET")
    void testProviderType() {
        assertEquals(ShippingProviderType.SHIPROCKET, provider.getProviderType());
    }

    @Test
    @DisplayName("Should successfully create automated shipment and assign AWB")
    void testCreateShipment() {
        ShipmentCreateCommand command = ShipmentCreateCommand.builder()
                .orderId(101L)
                .orderNumber("SC-TEST-9921")
                .recipientName("Aarav Mehta")
                .recipientPhone("9876543210")
                .recipientEmail("aarav.mehta@example.com")
                .shippingAddress("Flat 12B, Imperial Heights, Worli")
                .city("Mumbai")
                .state("Maharashtra")
                .postalCode("400018")
                .country("India")
                .orderTotal(new BigDecimal("12500.00"))
                .items(List.of(
                        ShipmentItemDto.builder()
                                .name("Bois Mystique Extrait")
                                .sku("SC-BM-100")
                                .quantity(1)
                                .price(new BigDecimal("12500.00"))
                                .build()
                ))
                .build();

        ShipmentCreateResult result = provider.createShipment(command);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(ShippingProviderType.SHIPROCKET, result.getProvider());
        assertEquals(ShipmentStatus.DISPATCHED, result.getStatus());
        assertNotNull(result.getTrackingNumber());
        assertTrue(result.getTrackingNumber().startsWith("SR-"));
        assertNotNull(result.getCarrierName());
        assertNotNull(result.getEstimatedDelivery());
    }
}
