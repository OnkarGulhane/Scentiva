package com.scentiva.modules.shipping.dto;

import com.scentiva.modules.shipping.model.ShipmentStatus;
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
public class ShipmentTrackingResponse {
    private String orderNumber;
    private String trackingNumber;
    private String carrierName;
    private ShipmentStatus currentStatus;
    private LocalDateTime estimatedDelivery;
    private List<ShipmentEventResponse> timeline;
}
