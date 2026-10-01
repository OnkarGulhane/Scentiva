package com.scentiva.modules.inventory.repository;

import com.scentiva.modules.catalog.model.*;
import com.scentiva.modules.catalog.repository.BrandRepository;
import com.scentiva.modules.catalog.repository.CategoryRepository;
import com.scentiva.modules.catalog.repository.ProductRepository;
import com.scentiva.modules.catalog.repository.ProductVariantRepository;
import com.scentiva.modules.inventory.model.InventoryMovement;
import com.scentiva.modules.inventory.model.InventoryRecord;
import com.scentiva.modules.inventory.model.MovementReason;
import com.scentiva.modules.inventory.model.Warehouse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class InventoryRepositoryTest {

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductVariantRepository productVariantRepository;

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private InventoryRecordRepository inventoryRecordRepository;

    @Autowired
    private InventoryMovementRepository inventoryMovementRepository;

    @Test
    @DisplayName("Should persist multi-location inventory, reserve stock, and audit movements")
    void shouldHandleMultiLocationInventoryOperations() {
        // 1. Setup Brand & Product Variant
        Brand creed = brandRepository.save(Brand.builder()
                .name("Creed")
                .slug("creed")
                .originCountry("France")
                .tier(BrandTier.HERITAGE_MAISON)
                .build());

        Category luxury = categoryRepository.save(Category.builder()
                .name("Luxury & Niche")
                .slug("luxury-niche")
                .build());

        Product aventus = Product.builder()
                .brand(creed)
                .category(luxury)
                .name("Aventus")
                .slug("creed-aventus")
                .build();

        ProductVariant variant100ml = ProductVariant.builder()
                .sku("CREED-AVENT-100ML")
                .volumeMl(100)
                .basePrice(new BigDecimal("32000.00"))
                .build();
        aventus.addVariant(variant100ml);
        Product savedProduct = productRepository.save(aventus);
        variant100ml = savedProduct.getVariants().get(0);

        // 2. Setup 2 Warehouses (Pune Main Hub & Mumbai Express Hub)
        Warehouse puneWarehouse = warehouseRepository.save(Warehouse.builder()
                .code("WH-PUNE-01")
                .name("Pune Central Luxury Hub")
                .city("Pune")
                .state("Maharashtra")
                .build());

        Warehouse mumbaiWarehouse = warehouseRepository.save(Warehouse.builder()
                .code("WH-MUMBAI-01")
                .name("Mumbai Coastal Express Hub")
                .city("Mumbai")
                .state("Maharashtra")
                .build());

        // 3. Stock in Pune (Qty: 25, Reserved: 0) and Mumbai (Qty: 10, Reserved: 2)
        InventoryRecord puneRecord = inventoryRecordRepository.save(InventoryRecord.builder()
                .variant(variant100ml)
                .warehouse(puneWarehouse)
                .quantityOnHand(25)
                .quantityReserved(0)
                .lowStockThreshold(5)
                .build());

        InventoryRecord mumbaiRecord = inventoryRecordRepository.save(InventoryRecord.builder()
                .variant(variant100ml)
                .warehouse(mumbaiWarehouse)
                .quantityOnHand(10)
                .quantityReserved(2)
                .lowStockThreshold(5)
                .build());

        // 4. Verify Available Quantities
        assertEquals(25, puneRecord.getAvailableQuantity());
        assertEquals(8, mumbaiRecord.getAvailableQuantity());

        // 5. Test Stock Reservation in Pune
        puneRecord.reserve(5);
        inventoryRecordRepository.save(puneRecord);
        assertEquals(20, puneRecord.getAvailableQuantity());
        assertEquals(5, puneRecord.getQuantityReserved());

        // 6. Record Inventory Movement Audit
        InventoryMovement movement = inventoryMovementRepository.save(InventoryMovement.builder()
                .variant(variant100ml)
                .warehouse(puneWarehouse)
                .changeQuantity(-5)
                .previousQuantity(25)
                .newQuantity(20)
                .reason(MovementReason.RESERVATION)
                .referenceId("CHK-ORD-TEST-101")
                .build());

        assertNotNull(movement.getId());
        List<InventoryMovement> movements = inventoryMovementRepository.findByVariantIdOrderByCreatedAtDesc(variant100ml.getId());
        assertEquals(1, movements.size());
        assertEquals(MovementReason.RESERVATION, movements.get(0).getReason());

        // 7. Test Pessimistic Lock Query (works on JPA repository)
        Optional<InventoryRecord> lockedRecord = inventoryRecordRepository.findWithLockByVariantIdAndWarehouseId(
                variant100ml.getId(), puneWarehouse.getId());
        assertTrue(lockedRecord.isPresent());
        assertEquals(25, lockedRecord.get().getQuantityOnHand());
    }
}
