package com.scentiva.modules.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockReservationResponse {
    private String referenceId;
    private Long variantId;
    private Long warehouseId;
    private int quantity;
    private LocalDateTime reservedAt;
    private LocalDateTime expiresAt;
    private boolean success;
    private String message;
}
