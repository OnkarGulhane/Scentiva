package com.scentiva.modules.inventory.repository;

import com.scentiva.modules.inventory.model.InventoryRecord;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRecordRepository extends JpaRepository<InventoryRecord, Long> {

    Optional<InventoryRecord> findByVariantIdAndWarehouseIdAndIsDeletedFalse(Long variantId, Long warehouseId);

    List<InventoryRecord> findByVariantIdAndIsDeletedFalse(Long variantId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT ir FROM InventoryRecord ir WHERE ir.variant.id = :variantId AND ir.warehouse.id = :warehouseId AND ir.isDeleted = false")
    Optional<InventoryRecord> findWithLockByVariantIdAndWarehouseId(@Param("variantId") Long variantId, @Param("warehouseId") Long warehouseId);

    @Query("SELECT ir FROM InventoryRecord ir WHERE ir.quantityOnHand - ir.quantityReserved <= ir.lowStockThreshold AND ir.isDeleted = false")
    List<InventoryRecord> findLowStockRecords();
}
