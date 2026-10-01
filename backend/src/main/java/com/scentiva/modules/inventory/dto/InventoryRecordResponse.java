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
public class InventoryRecordResponse {
    private Long id;
    private Long variantId;
    private String sku;
    private String productName;
    private Long warehouseId;
    private String warehouseCode;
    private String warehouseName;
    private Integer quantityOnHand;
    private Integer quantityReserved;
    private Integer availableQuantity;
    private Integer lowStockThreshold;
    private Boolean isLowStock;
    private LocalDateTime updatedAt;
}
