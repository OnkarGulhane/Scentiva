package com.scentiva.modules.shipping.dto;

import com.scentiva.modules.shipping.model.ShipmentStatus;
import com.scentiva.modules.shipping.model.ShippingProviderType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentResponse {
    private Long id;
    private Long orderId;
    private String orderNumber;
    private ShippingProviderType provider;
    private String carrierName;
    private String trackingNumber;
    private ShipmentStatus status;
    private LocalDateTime dispatchedAt;
    private LocalDateTime deliveredAt;
    private List<ShipmentEventResponse> events;
    private LocalDateTime createdAt;
}
