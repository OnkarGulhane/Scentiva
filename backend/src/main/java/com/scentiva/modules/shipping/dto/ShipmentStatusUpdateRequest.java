package com.scentiva.modules.shipping.dto;

import com.scentiva.modules.shipping.model.ShipmentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentStatusUpdateRequest {

    @NotNull(message = "Status is required")
    private ShipmentStatus status;

    private String location;
    private String description;
}
