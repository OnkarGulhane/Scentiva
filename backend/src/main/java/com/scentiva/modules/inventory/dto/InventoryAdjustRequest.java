package com.scentiva.modules.inventory.dto;

import com.scentiva.modules.inventory.model.MovementReason;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryAdjustRequest {

    @NotNull(message = "Variant ID is required")
    private Long variantId;

    @NotNull(message = "Warehouse ID is required")
    private Long warehouseId;

    @NotNull(message = "Change quantity is required")
    private Integer changeQuantity;

    @NotNull(message = "Movement reason is required")
    private MovementReason reason;

    private String referenceId;
}
