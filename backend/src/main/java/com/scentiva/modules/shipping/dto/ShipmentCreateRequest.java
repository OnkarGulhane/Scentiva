package com.scentiva.modules.shipping.dto;

import com.scentiva.modules.shipping.model.ShippingProviderType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentCreateRequest {

    @NotNull(message = "Order ID is required")
    private Long orderId;

    @Builder.Default
    private ShippingProviderType provider = ShippingProviderType.MANUAL;

    private String carrierName;
    private String customTrackingNumber;
    private String dispatchLocation;
    private String notes;
}
