package com.scentiva.modules.inventory.repository;

import com.scentiva.modules.inventory.model.InventoryMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryMovementRepository extends JpaRepository<InventoryMovement, Long> {

    List<InventoryMovement> findByVariantIdOrderByCreatedAtDesc(Long variantId);

    List<InventoryMovement> findByWarehouseIdOrderByCreatedAtDesc(Long warehouseId);
}
