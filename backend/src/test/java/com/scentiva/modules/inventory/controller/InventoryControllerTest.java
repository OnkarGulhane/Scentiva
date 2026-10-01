package com.scentiva.modules.inventory.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scentiva.modules.catalog.model.*;
import com.scentiva.modules.catalog.repository.BrandRepository;
import com.scentiva.modules.catalog.repository.CategoryRepository;
import com.scentiva.modules.catalog.repository.ProductRepository;
import com.scentiva.modules.catalog.repository.ProductVariantRepository;
import com.scentiva.modules.inventory.dto.InventoryAdjustRequest;
import com.scentiva.modules.inventory.dto.StockReservationRequest;
import com.scentiva.modules.inventory.model.InventoryRecord;
import com.scentiva.modules.inventory.model.MovementReason;
import com.scentiva.modules.inventory.model.Warehouse;
import com.scentiva.modules.inventory.repository.InventoryRecordRepository;
import com.scentiva.modules.inventory.repository.WarehouseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class InventoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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

    private ProductVariant variant;
    private Warehouse warehouse;
    private InventoryRecord inventoryRecord;

    @BeforeEach
    void setUp() {
        if (warehouseRepository.findByCodeAndIsDeletedFalse("WH-INV-TEST").isEmpty()) {
            warehouse = warehouseRepository.save(Warehouse.builder()
                    .code("WH-INV-TEST")
                    .name("Test Inventory Warehouse")
                    .city("Pune")
                    .state("Maharashtra")
                    .isActive(true)
                    .build());
        } else {
            warehouse = warehouseRepository.findByCodeAndIsDeletedFalse("WH-INV-TEST").get();
        }

        Brand brand = brandRepository.findBySlugAndIsDeletedFalse("creed-inv")
                .orElseGet(() -> brandRepository.save(Brand.builder()
                        .name("Creed Inv")
                        .slug("creed-inv")
                        .originCountry("France")
                        .tier(BrandTier.HERITAGE_MAISON)
                        .build()));

        Category category = categoryRepository.findBySlugAndIsDeletedFalse("fresh-citrus-inv")
                .orElseGet(() -> categoryRepository.save(Category.builder()
                        .name("Fresh Citrus Inv")
                        .slug("fresh-citrus-inv")
                        .build()));

        Product product = productRepository.findBySlugAndIsDeletedFalse("aventus-cologne-inv")
                .orElseGet(() -> productRepository.save(Product.builder()
                        .brand(brand)
                        .category(category)
                        .name("Aventus Cologne Inv")
                        .slug("aventus-cologne-inv")
                        .description("Fresh modern masterwork")
                        .gender(GenderTarget.UNISEX)
                        .isActive(true)
                        .build()));

        variant = productVariantRepository.findBySkuAndIsDeletedFalse("CR-AV-100-TEST")
                .orElseGet(() -> productVariantRepository.save(ProductVariant.builder()
                        .product(product)
                        .sku("CR-AV-100-TEST")
                        .volumeMl(100)
                        .concentration(Concentration.EDP)
                        .basePrice(new BigDecimal("420.00"))
                        .salePrice(new BigDecimal("390.00"))
                        .isActive(true)
                        .build()));

        inventoryRecord = inventoryRecordRepository.findByVariantIdAndWarehouseIdAndIsDeletedFalse(variant.getId(), warehouse.getId())
                .orElseGet(() -> inventoryRecordRepository.save(InventoryRecord.builder()
                        .variant(variant)
                        .warehouse(warehouse)
                        .quantityOnHand(100)
                        .quantityReserved(10)
                        .lowStockThreshold(5)
                        .build()));
    }

    @Test
    @DisplayName("GET /api/v1/inventory/variant/{variantId} should return inventory list")
    void shouldGetInventoryByVariant() throws Exception {
        mockMvc.perform(get("/api/v1/inventory/variant/" + variant.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data[0].sku", is(variant.getSku())));
    }

    @Test
    @DisplayName("GET /api/v1/inventory/variant/{variantId}/available should return total available count")
    void shouldGetTotalAvailableStock() throws Exception {
        mockMvc.perform(get("/api/v1/inventory/variant/" + variant.getId() + "/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data").isNumber());
    }

    @Test
    @DisplayName("POST /api/v1/inventory/reserve should reserve stock successfully")
    void shouldReserveStock() throws Exception {
        StockReservationRequest request = StockReservationRequest.builder()
                .variantId(variant.getId())
                .warehouseId(warehouse.getId())
                .quantity(2)
                .referenceId("TEST-CHK-RES-01")
                .build();

        mockMvc.perform(post("/api/v1/inventory/reserve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.quantity", is(2)))
                .andExpect(jsonPath("$.data.referenceId", is("TEST-CHK-RES-01")));
    }

    @Test
    @DisplayName("POST /api/v1/inventory/adjust (Admin) should adjust stock quantity")
    @WithMockUser(roles = "ADMIN")
    void shouldAdjustStockAsAdmin() throws Exception {
        InventoryAdjustRequest request = InventoryAdjustRequest.builder()
                .variantId(variant.getId())
                .warehouseId(warehouse.getId())
                .changeQuantity(15)
                .reason(MovementReason.RECEIPT)
                .referenceId("PO-TEST-101")
                .build();

        mockMvc.perform(post("/api/v1/inventory/adjust")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.quantityOnHand").isNumber());
    }

    @Test
    @DisplayName("GET /api/v1/inventory/low-stock (Admin) should return low stock alerts")
    @WithMockUser(roles = "ADMIN")
    void shouldGetLowStockAlertsAsAdmin() throws Exception {
        mockMvc.perform(get("/api/v1/inventory/low-stock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));
    }
}
