package com.scentiva.stress;

import com.scentiva.common.exception.InsufficientStockException;
import com.scentiva.modules.catalog.model.*;
import com.scentiva.modules.catalog.repository.*;
import com.scentiva.modules.inventory.dto.StockReservationRequest;
import com.scentiva.modules.inventory.model.InventoryRecord;
import com.scentiva.modules.inventory.model.Warehouse;
import com.scentiva.modules.inventory.repository.InventoryRecordRepository;
import com.scentiva.modules.inventory.repository.WarehouseRepository;
import com.scentiva.modules.inventory.service.InventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class InventoryConcurrencyStressTest {

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private InventoryRecordRepository inventoryRecordRepository;

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductVariantRepository productVariantRepository;

    private ProductVariant variant;
    private Warehouse warehouse;

    @BeforeEach
    void setUp() {
        Brand brand = brandRepository.findBySlugAndIsDeletedFalse("roja-parfums-stress")
                .orElseGet(() -> brandRepository.save(Brand.builder()
                        .name("Roja Parfums Stress")
                        .slug("roja-parfums-stress")
                        .originCountry("United Kingdom")
                        .tier(BrandTier.HERITAGE_MAISON)
                        .build()));

        Category category = categoryRepository.findBySlugAndIsDeletedFalse("oriental-stress")
                .orElseGet(() -> categoryRepository.save(Category.builder()
                        .name("Oriental Stress")
                        .slug("oriental-stress")
                        .build()));

        Product product = productRepository.findBySlugAndIsDeletedFalse("elysium-stress")
                .orElseGet(() -> productRepository.save(Product.builder()
                        .brand(brand)
                        .category(category)
                        .name("Elysium Parfum Stress")
                        .slug("elysium-stress")
                        .gender(GenderTarget.UNISEX)
                        .isActive(true)
                        .build()));

        variant = productVariantRepository.findBySkuAndIsDeletedFalse("ROJ-ELY-50-STRESS")
                .orElseGet(() -> productVariantRepository.save(ProductVariant.builder()
                        .product(product)
                        .sku("ROJ-ELY-50-STRESS")
                        .volumeMl(50)
                        .concentration(Concentration.PARFUM)
                        .basePrice(new BigDecimal("38000.00"))
                        .isActive(true)
                        .build()));

        warehouse = warehouseRepository.findByCodeAndIsDeletedFalse("WH-STRESS-PUN")
                .orElseGet(() -> warehouseRepository.save(Warehouse.builder()
                        .name("Pune Central Vault Stress")
                        .code("WH-STRESS-PUN")
                        .city("Pune")
                        .state("Maharashtra")
                        .isActive(true)
                        .build()));

        // Reset inventory record to strictly 5 units on hand, 0 reserved
        InventoryRecord record = inventoryRecordRepository
                .findByVariantIdAndWarehouseIdAndIsDeletedFalse(variant.getId(), warehouse.getId())
                .orElseGet(() -> InventoryRecord.builder()
                        .variant(variant)
                        .warehouse(warehouse)
                        .quantityOnHand(5)
                        .quantityReserved(0)
                        .lowStockThreshold(2)
                        .build());

        record.setQuantityOnHand(5);
        record.setQuantityReserved(0);
        inventoryRecordRepository.save(record);
    }

    @Test
    @DisplayName("Stress Test: 100 concurrent threads reserving scarce stock (5 units) must result in exactly 5 successes and 95 failures")
    void shouldHandle100ConcurrentStockReservationsWithoutOverselling() throws InterruptedException {
        int totalThreads = 100;
        ExecutorService executor = Executors.newFixedThreadPool(20);
        CountDownLatch startGun = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(totalThreads);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        for (int i = 0; i < totalThreads; i++) {
            final int index = i;
            executor.submit(() -> {
                try {
                    startGun.await(); // wait for simultaneous trigger
                    StockReservationRequest request = StockReservationRequest.builder()
                            .variantId(variant.getId())
                            .warehouseId(warehouse.getId())
                            .quantity(1)
                            .referenceId("stress-ref-" + index)
                            .build();

                    inventoryService.reserveStock(request);
                    successCount.incrementAndGet();
                } catch (InsufficientStockException ex) {
                    failureCount.incrementAndGet();
                } catch (Exception ex) {
                    failureCount.incrementAndGet();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        startGun.countDown(); // FIRE all 100 threads simultaneously
        boolean completed = doneLatch.await(30, TimeUnit.SECONDS);
        executor.shutdown();

        assertThat(completed).isTrue();
        assertThat(successCount.get()).isEqualTo(5);
        assertThat(failureCount.get()).isEqualTo(95);

        // Verify final persistent inventory state
        int availableStock = inventoryService.getTotalAvailableStock(variant.getId());
        assertThat(availableStock).isEqualTo(0);

        InventoryRecord finalRecord = inventoryRecordRepository
                .findByVariantIdAndWarehouseIdAndIsDeletedFalse(variant.getId(), warehouse.getId())
                .orElseThrow();

        assertThat(finalRecord.getQuantityOnHand()).isEqualTo(5);
        assertThat(finalRecord.getQuantityReserved()).isEqualTo(5);
        assertThat(finalRecord.getAvailableQuantity()).isEqualTo(0);
    }
}
