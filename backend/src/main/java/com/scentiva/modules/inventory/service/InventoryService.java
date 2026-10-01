package com.scentiva.modules.inventory.service;

import com.scentiva.modules.inventory.dto.*;

import java.util.List;

public interface InventoryService {

    List<InventoryRecordResponse> getInventoryByVariant(Long variantId);

    int getTotalAvailableStock(Long variantId);

    List<InventoryRecordResponse> getLowStockAlerts();

    InventoryRecordResponse adjustStock(InventoryAdjustRequest request);

    StockReservationResponse reserveStock(StockReservationRequest request);

    void releaseStockReservation(Long variantId, Long warehouseId, int quantity, String referenceId);

    void deductStock(Long variantId, Long warehouseId, int quantity, String referenceId);

    List<InventoryMovementResponse> getMovementsByVariant(Long variantId);
}
