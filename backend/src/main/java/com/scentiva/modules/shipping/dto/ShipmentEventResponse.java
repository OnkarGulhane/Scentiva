package com.scentiva.modules.shipping.dto;

import com.scentiva.modules.shipping.model.ShipmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentEventResponse {
    private Long id;
    private ShipmentStatus status;
    private String location;
    private String description;
    private LocalDateTime eventTimestamp;
}
