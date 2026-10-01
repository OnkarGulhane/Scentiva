package com.scentiva.modules.inventory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockReservationRequest {

    @NotNull(message = "Variant ID is required")
    private Long variantId;

    private Long warehouseId;

    @Min(value = 1, message = "Reservation quantity must be at least 1")
    private int quantity;

    @NotBlank(message = "Reference ID (e.g. checkout or cart token) is required")
    private String referenceId;
}
