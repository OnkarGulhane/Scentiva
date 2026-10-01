package com.scentiva.modules.inventory.dto;

import com.scentiva.modules.inventory.model.MovementReason;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryMovementResponse {
    private Long id;
    private Long variantId;
    private String sku;
    private Long warehouseId;
    private String warehouseCode;
    private Integer changeQuantity;
    private Integer previousQuantity;
    private Integer newQuantity;
    private MovementReason reason;
    private String referenceId;
    private LocalDateTime createdAt;
}
