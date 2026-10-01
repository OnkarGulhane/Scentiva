package com.scentiva.modules.inventory.service.impl;

import com.scentiva.common.exception.InsufficientStockException;
import com.scentiva.common.exception.ResourceNotFoundException;
import com.scentiva.modules.catalog.model.ProductVariant;
import com.scentiva.modules.catalog.repository.ProductVariantRepository;
import com.scentiva.modules.inventory.dto.*;
import com.scentiva.modules.inventory.model.InventoryMovement;
import com.scentiva.modules.inventory.model.InventoryRecord;
import com.scentiva.modules.inventory.model.MovementReason;
import com.scentiva.modules.inventory.model.Warehouse;
import com.scentiva.modules.inventory.repository.InventoryMovementRepository;
import com.scentiva.modules.inventory.repository.InventoryRecordRepository;
import com.scentiva.modules.inventory.repository.WarehouseRepository;
import com.scentiva.modules.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRecordRepository inventoryRecordRepository;
    private final InventoryMovementRepository inventoryMovementRepository;
    private final WarehouseRepository warehouseRepository;
    private final ProductVariantRepository productVariantRepository;

    @Override
    @Transactional(readOnly = true)
    public List<InventoryRecordResponse> getInventoryByVariant(Long variantId) {
        return inventoryRecordRepository.findByVariantIdAndIsDeletedFalse(variantId).stream()
                .map(this::mapToRecordResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public int getTotalAvailableStock(Long variantId) {
        return inventoryRecordRepository.findByVariantIdAndIsDeletedFalse(variantId).stream()
                .mapToInt(InventoryRecord::getAvailableQuantity)
                .sum();
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryRecordResponse> getLowStockAlerts() {
        return inventoryRecordRepository.findLowStockRecords().stream()
                .map(this::mapToRecordResponse)
                .toList();
    }

    @Override
    @Transactional
    public InventoryRecordResponse adjustStock(InventoryAdjustRequest request) {
        ProductVariant variant = productVariantRepository.findById(request.getVariantId())
                .filter(v -> !v.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("ProductVariant", "id", request.getVariantId()));

        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .filter(w -> !w.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", "id", request.getWarehouseId()));

        InventoryRecord record = inventoryRecordRepository.findWithLockByVariantIdAndWarehouseId(variant.getId(), warehouse.getId())
                .orElseGet(() -> {
                    InventoryRecord newRecord = InventoryRecord.builder()
                            .variant(variant)
                            .warehouse(warehouse)
                            .quantityOnHand(0)
                            .quantityReserved(0)
                            .lowStockThreshold(5)
                            .build();
                    return inventoryRecordRepository.save(newRecord);
                });

        int prevOnHand = record.getQuantityOnHand();
        int change = request.getChangeQuantity();

        if (change > 0) {
            record.addStock(change);
        } else if (change < 0) {
            int toDeduct = Math.abs(change);
            if (prevOnHand < toDeduct) {
                throw new InsufficientStockException(variant.getSku(), toDeduct, prevOnHand);
            }
            if (record.getAvailableQuantity() < toDeduct) {
                throw new IllegalArgumentException("Cannot deduct stock below reserved amount for SKU " + variant.getSku());
            }
            record.setQuantityOnHand(prevOnHand - toDeduct);
        }

        record = inventoryRecordRepository.save(record);

        // Record immutable movement
        InventoryMovement movement = InventoryMovement.builder()
                .variant(variant)
                .warehouse(warehouse)
                .changeQuantity(change)
                .previousQuantity(prevOnHand)
                .newQuantity(record.getQuantityOnHand())
                .reason(request.getReason() != null ? request.getReason() : MovementReason.ADJUSTMENT)
                .referenceId(request.getReferenceId())
                .build();
        inventoryMovementRepository.save(movement);

        log.info("Adjusted stock for variant SKU={}, warehouse={}, change={}, newOnHand={}",
                variant.getSku(), warehouse.getCode(), change, record.getQuantityOnHand());

        return mapToRecordResponse(record);
    }

    @Override
    @Transactional
    public StockReservationResponse reserveStock(StockReservationRequest request) {
        ProductVariant variant = productVariantRepository.findById(request.getVariantId())
                .filter(v -> !v.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("ProductVariant", "id", request.getVariantId()));

        InventoryRecord targetRecord;

        if (request.getWarehouseId() != null) {
            targetRecord = inventoryRecordRepository.findWithLockByVariantIdAndWarehouseId(variant.getId(), request.getWarehouseId())
                    .orElseThrow(() -> new ResourceNotFoundException("InventoryRecord", "warehouseId", request.getWarehouseId()));
            if (targetRecord.getAvailableQuantity() < request.getQuantity()) {
                throw new InsufficientStockException(variant.getSku(), request.getQuantity(), targetRecord.getAvailableQuantity());
            }
        } else {
            // Pick warehouse with highest available stock
            List<InventoryRecord> records = inventoryRecordRepository.findByVariantIdAndIsDeletedFalse(variant.getId());
            InventoryRecord bestCandidate = records.stream()
                    .filter(r -> r.getAvailableQuantity() >= request.getQuantity())
                    .findFirst()
                    .orElseThrow(() -> new InsufficientStockException(variant.getSku(), request.getQuantity(), getTotalAvailableStock(variant.getId())));

            // Re-fetch candidate with pessimistic lock
            targetRecord = inventoryRecordRepository.findWithLockByVariantIdAndWarehouseId(variant.getId(), bestCandidate.getWarehouse().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("InventoryRecord", "variantId", variant.getId()));

            if (targetRecord.getAvailableQuantity() < request.getQuantity()) {
                throw new InsufficientStockException(variant.getSku(), request.getQuantity(), targetRecord.getAvailableQuantity());
            }
        }

        targetRecord.reserve(request.getQuantity());
        inventoryRecordRepository.save(targetRecord);

        // Record reservation movement
        InventoryMovement movement = InventoryMovement.builder()
                .variant(variant)
                .warehouse(targetRecord.getWarehouse())
                .changeQuantity(0) // On hand does not change immediately during reservation
                .previousQuantity(targetRecord.getQuantityOnHand())
                .newQuantity(targetRecord.getQuantityOnHand())
                .reason(MovementReason.RESERVATION)
                .referenceId(request.getReferenceId())
                .build();
        inventoryMovementRepository.save(movement);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusMinutes(15); // 15-minute stock hold TTL

        log.info("Reserved {} units of variant SKU={} at warehouse={}, referenceId={}",
                request.getQuantity(), variant.getSku(), targetRecord.getWarehouse().getCode(), request.getReferenceId());

        return StockReservationResponse.builder()
                .referenceId(request.getReferenceId())
                .variantId(variant.getId())
                .warehouseId(targetRecord.getWarehouse().getId())
                .quantity(request.getQuantity())
                .reservedAt(now)
                .expiresAt(expiresAt)
                .success(true)
                .message("Stock reserved successfully for 15 minutes")
                .build();
    }

    @Override
    @Transactional
    public void releaseStockReservation(Long variantId, Long warehouseId, int quantity, String referenceId) {
        InventoryRecord record;
        if (warehouseId != null) {
            record = inventoryRecordRepository.findWithLockByVariantIdAndWarehouseId(variantId, warehouseId)
                    .orElseThrow(() -> new ResourceNotFoundException("InventoryRecord", "variantId", variantId));
        } else {
            List<InventoryRecord> records = inventoryRecordRepository.findByVariantIdAndIsDeletedFalse(variantId);
            InventoryRecord candidate = records.stream()
                    .filter(r -> r.getQuantityReserved() >= quantity)
                    .findFirst()
                    .orElseGet(() -> records.stream().findFirst().orElseThrow(() -> new ResourceNotFoundException("InventoryRecord", "variantId", variantId)));

            record = inventoryRecordRepository.findWithLockByVariantIdAndWarehouseId(variantId, candidate.getWarehouse().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("InventoryRecord", "variantId", variantId));
        }

        record.releaseReservation(quantity);
        inventoryRecordRepository.save(record);

        InventoryMovement movement = InventoryMovement.builder()
                .variant(record.getVariant())
                .warehouse(record.getWarehouse())
                .changeQuantity(0)
                .previousQuantity(record.getQuantityOnHand())
                .newQuantity(record.getQuantityOnHand())
                .reason(MovementReason.RELEASE)
                .referenceId(referenceId)
                .build();
        inventoryMovementRepository.save(movement);

        log.info("Released reservation of {} units for SKU={} at warehouse={}, referenceId={}",
                quantity, record.getVariant().getSku(), record.getWarehouse().getCode(), referenceId);
    }

    @Override
    @Transactional
    public void deductStock(Long variantId, Long warehouseId, int quantity, String referenceId) {
        InventoryRecord record;
        if (warehouseId != null) {
            record = inventoryRecordRepository.findWithLockByVariantIdAndWarehouseId(variantId, warehouseId)
                    .orElseThrow(() -> new ResourceNotFoundException("InventoryRecord", "variantId", variantId));
        } else {
            List<InventoryRecord> records = inventoryRecordRepository.findByVariantIdAndIsDeletedFalse(variantId);
            InventoryRecord candidate = records.stream()
                    .filter(r -> r.getQuantityOnHand() >= quantity)
                    .findFirst()
                    .orElseGet(() -> records.stream().findFirst().orElseThrow(() -> new ResourceNotFoundException("InventoryRecord", "variantId", variantId)));

            record = inventoryRecordRepository.findWithLockByVariantIdAndWarehouseId(variantId, candidate.getWarehouse().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("InventoryRecord", "variantId", variantId));
        }

        int prevOnHand = record.getQuantityOnHand();
        record.deduct(quantity);
        inventoryRecordRepository.save(record);

        InventoryMovement movement = InventoryMovement.builder()
                .variant(record.getVariant())
                .warehouse(record.getWarehouse())
                .changeQuantity(-quantity)
                .previousQuantity(prevOnHand)
                .newQuantity(record.getQuantityOnHand())
                .reason(MovementReason.SALE)
                .referenceId(referenceId)
                .build();
        inventoryMovementRepository.save(movement);

        log.info("Deducted {} units from SKU={} at warehouse={}, newOnHand={}, referenceId={}",
                quantity, record.getVariant().getSku(), record.getWarehouse().getCode(), record.getQuantityOnHand(), referenceId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryMovementResponse> getMovementsByVariant(Long variantId) {
        return inventoryMovementRepository.findByVariantIdOrderByCreatedAtDesc(variantId).stream()
                .map(this::mapToMovementResponse)
                .toList();
    }

    private InventoryRecordResponse mapToRecordResponse(InventoryRecord record) {
        return InventoryRecordResponse.builder()
                .id(record.getId())
                .variantId(record.getVariant().getId())
                .sku(record.getVariant().getSku())
                .productName(record.getVariant().getProduct() != null ? record.getVariant().getProduct().getName() : null)
                .warehouseId(record.getWarehouse().getId())
                .warehouseCode(record.getWarehouse().getCode())
                .warehouseName(record.getWarehouse().getName())
                .quantityOnHand(record.getQuantityOnHand())
                .quantityReserved(record.getQuantityReserved())
                .availableQuantity(record.getAvailableQuantity())
                .lowStockThreshold(record.getLowStockThreshold())
                .isLowStock(record.isLowStock())
                .updatedAt(record.getUpdatedAt())
                .build();
    }

    private InventoryMovementResponse mapToMovementResponse(InventoryMovement movement) {
        return InventoryMovementResponse.builder()
                .id(movement.getId())
                .variantId(movement.getVariant().getId())
                .sku(movement.getVariant().getSku())
                .warehouseId(movement.getWarehouse().getId())
                .warehouseCode(movement.getWarehouse().getCode())
                .changeQuantity(movement.getChangeQuantity())
                .previousQuantity(movement.getPreviousQuantity())
                .newQuantity(movement.getNewQuantity())
                .reason(movement.getReason())
                .referenceId(movement.getReferenceId())
                .createdAt(movement.getCreatedAt())
                .build();
    }
}
