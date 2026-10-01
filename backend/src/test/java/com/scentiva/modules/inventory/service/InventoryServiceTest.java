package com.scentiva.modules.inventory.service;

import com.scentiva.common.exception.InsufficientStockException;
import com.scentiva.modules.catalog.model.Concentration;
import com.scentiva.modules.catalog.model.Product;
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
import com.scentiva.modules.inventory.service.impl.InventoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private InventoryRecordRepository inventoryRecordRepository;

    @Mock
    private InventoryMovementRepository inventoryMovementRepository;

    @Mock
    private WarehouseRepository warehouseRepository;

    @Mock
    private ProductVariantRepository productVariantRepository;

    @InjectMocks
    private InventoryServiceImpl inventoryService;

    private Product product;
    private ProductVariant variant;
    private Warehouse warehouse;
    private InventoryRecord record;

    @BeforeEach
    void setUp() {
        product = Product.builder()
                .name("Oud Wood Extrait")
                .slug("oud-wood-extrait")
                .build();
        product.setId(10L);

        variant = ProductVariant.builder()
                .product(product)
                .sku("TF-OW-100")
                .volumeMl(100)
                .concentration(Concentration.EDP)
                .basePrice(new BigDecimal("350.00"))
                .salePrice(new BigDecimal("320.00"))
                .isActive(true)
                .build();
        variant.setId(100L);
        variant.setDeleted(false);

        warehouse = Warehouse.builder()
                .code("WH-PUN-01")
                .name("Pune Hub")
                .city("Pune")
                .state("MH")
                .isActive(true)
                .build();
        warehouse.setId(1L);
        warehouse.setDeleted(false);

        record = InventoryRecord.builder()
                .variant(variant)
                .warehouse(warehouse)
                .quantityOnHand(50)
                .quantityReserved(5)
                .lowStockThreshold(10)
                .build();
        record.setId(500L);
        record.setDeleted(false);
    }

    @Test
    @DisplayName("Should return available inventory by variant")
    void shouldGetInventoryByVariant() {
        when(inventoryRecordRepository.findByVariantIdAndIsDeletedFalse(100L))
                .thenReturn(List.of(record));

        List<InventoryRecordResponse> responses = inventoryService.getInventoryByVariant(100L);

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getAvailableQuantity()).isEqualTo(45);
        assertThat(responses.get(0).getSku()).isEqualTo("TF-OW-100");
    }

    @Test
    @DisplayName("Should calculate total available stock across warehouses")
    void shouldGetTotalAvailableStock() {
        Warehouse wh2 = Warehouse.builder().code("WH-DEL-01").name("Delhi").build();
        wh2.setId(2L);
        InventoryRecord record2 = InventoryRecord.builder()
                .variant(variant)
                .warehouse(wh2)
                .quantityOnHand(30)
                .quantityReserved(0)
                .build();

        when(inventoryRecordRepository.findByVariantIdAndIsDeletedFalse(100L))
                .thenReturn(List.of(record, record2));

        int total = inventoryService.getTotalAvailableStock(100L);

        assertThat(total).isEqualTo(45 + 30); // 75
    }

    @Test
    @DisplayName("Should adjust stock and record movement ledger")
    void shouldAdjustStock() {
        InventoryAdjustRequest request = InventoryAdjustRequest.builder()
                .variantId(100L)
                .warehouseId(1L)
                .changeQuantity(20)
                .reason(MovementReason.RECEIPT)
                .referenceId("PO-2026-001")
                .build();

        when(productVariantRepository.findById(100L)).thenReturn(Optional.of(variant));
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(inventoryRecordRepository.findWithLockByVariantIdAndWarehouseId(100L, 1L)).thenReturn(Optional.of(record));
        when(inventoryRecordRepository.save(any(InventoryRecord.class))).thenAnswer(i -> i.getArgument(0));

        InventoryRecordResponse response = inventoryService.adjustStock(request);

        assertThat(response.getQuantityOnHand()).isEqualTo(70);
        verify(inventoryMovementRepository).save(any(InventoryMovement.class));
    }

    @Test
    @DisplayName("Should reserve stock with hold duration")
    void shouldReserveStock() {
        StockReservationRequest request = StockReservationRequest.builder()
                .variantId(100L)
                .warehouseId(1L)
                .quantity(3)
                .referenceId("CHK-SESSION-12345")
                .build();

        when(productVariantRepository.findById(100L)).thenReturn(Optional.of(variant));
        when(inventoryRecordRepository.findWithLockByVariantIdAndWarehouseId(100L, 1L)).thenReturn(Optional.of(record));
        when(inventoryRecordRepository.save(any(InventoryRecord.class))).thenAnswer(i -> i.getArgument(0));

        StockReservationResponse response = inventoryService.reserveStock(request);

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getQuantity()).isEqualTo(3);
        assertThat(record.getQuantityReserved()).isEqualTo(8); // 5 + 3
        assertThat(response.getExpiresAt()).isAfter(response.getReservedAt());
        verify(inventoryMovementRepository).save(any(InventoryMovement.class));
    }

    @Test
    @DisplayName("Should throw InsufficientStockException when reservation exceeds available stock")
    void shouldThrowWhenReservationExceedsAvailable() {
        StockReservationRequest request = StockReservationRequest.builder()
                .variantId(100L)
                .warehouseId(1L)
                .quantity(100) // available is 45
                .referenceId("CHK-SESSION-999")
                .build();

        when(productVariantRepository.findById(100L)).thenReturn(Optional.of(variant));
        when(inventoryRecordRepository.findWithLockByVariantIdAndWarehouseId(100L, 1L)).thenReturn(Optional.of(record));

        assertThatThrownBy(() -> inventoryService.reserveStock(request))
                .isInstanceOf(InsufficientStockException.class)
                .hasMessageContaining("TF-OW-100");
    }

    @Test
    @DisplayName("Should release stock reservation")
    void shouldReleaseStockReservation() {
        when(inventoryRecordRepository.findWithLockByVariantIdAndWarehouseId(100L, 1L)).thenReturn(Optional.of(record));

        inventoryService.releaseStockReservation(100L, 1L, 2, "CANCEL-001");

        assertThat(record.getQuantityReserved()).isEqualTo(3); // 5 - 2
        verify(inventoryRecordRepository).save(record);
        verify(inventoryMovementRepository).save(any(InventoryMovement.class));
    }

    @Test
    @DisplayName("Should deduct stock on order fulfillment")
    void shouldDeductStock() {
        when(inventoryRecordRepository.findWithLockByVariantIdAndWarehouseId(100L, 1L)).thenReturn(Optional.of(record));

        inventoryService.deductStock(100L, 1L, 5, "ORD-98765");

        assertThat(record.getQuantityOnHand()).isEqualTo(45); // 50 - 5
        assertThat(record.getQuantityReserved()).isEqualTo(0); // 5 - 5
        verify(inventoryRecordRepository).save(record);
        verify(inventoryMovementRepository).save(any(InventoryMovement.class));
    }

    @Test
    @DisplayName("Should retrieve low stock alert records")
    void shouldGetLowStockAlerts() {
        record.setQuantityOnHand(8);
        record.setQuantityReserved(0);
        record.setLowStockThreshold(10); // available 8 <= 10 -> Low stock!

        when(inventoryRecordRepository.findLowStockRecords()).thenReturn(List.of(record));

        List<InventoryRecordResponse> alerts = inventoryService.getLowStockAlerts();

        assertThat(alerts).hasSize(1);
        assertThat(alerts.get(0).getIsLowStock()).isTrue();
    }
}
