package com.scentiva.modules.shipping.provider.impl;

import com.scentiva.modules.shipping.model.ShipmentStatus;
import com.scentiva.modules.shipping.model.ShippingProviderType;
import com.scentiva.modules.shipping.provider.ShipmentCreateCommand;
import com.scentiva.modules.shipping.provider.ShipmentCreateResult;
import com.scentiva.modules.shipping.provider.ShippingProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@Slf4j
public class ManualShippingProvider implements ShippingProvider {

    @Override
    public ShippingProviderType getProviderType() {
        return ShippingProviderType.MANUAL;
    }

    @Override
    public ShipmentCreateResult createShipment(ShipmentCreateCommand command) {
        String carrier = command.getCarrierName() != null && !command.getCarrierName().trim().isEmpty()
                ? command.getCarrierName().trim()
                : "BlueDart Luxury Logistics";

        String tracking = command.getCustomTrackingNumber() != null && !command.getCustomTrackingNumber().trim().isEmpty()
                ? command.getCustomTrackingNumber().trim()
                : "SC-TRK-" + UUID.randomUUID().toString().substring(0, 10).toUpperCase();

        LocalDateTime estimated = LocalDateTime.now().plusDays(3);

        log.info("Created manual shipment for order={}, tracking={}, carrier={}", command.getOrderNumber(), tracking, carrier);

        return ShipmentCreateResult.builder()
                .provider(ShippingProviderType.MANUAL)
                .carrierName(carrier)
                .trackingNumber(tracking)
                .status(ShipmentStatus.DISPATCHED)
                .estimatedDelivery(estimated)
                .success(true)
                .rawResponse("{\"carrier\":\"" + carrier + "\",\"tracking\":\"" + tracking + "\",\"status\":\"DISPATCHED\"}")
                .message("Shipment generated successfully with carrier " + carrier)
                .build();
    }

    @Override
    public boolean cancelShipment(String trackingNumber) {
        log.info("Cancelled manual shipment for tracking={}", trackingNumber);
        return true;
    }
}
