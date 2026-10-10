package com.scentiva.modules.shipping.dto.shiprocket;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShiprocketAssignAwbRequest {

    @JsonProperty("shipment_id")
    private Long shipmentId;

    @JsonProperty("courier_id")
    private String courierId;
}
