package com.scentiva.modules.shipping.provider;

import com.scentiva.modules.shipping.model.ShipmentStatus;
import com.scentiva.modules.shipping.model.ShippingProviderType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentCreateResult {
    private ShippingProviderType provider;
    private String carrierName;
    private String trackingNumber;
    private ShipmentStatus status;
    private LocalDateTime estimatedDelivery;
    private boolean success;
    private String rawResponse;
    private String message;
}
