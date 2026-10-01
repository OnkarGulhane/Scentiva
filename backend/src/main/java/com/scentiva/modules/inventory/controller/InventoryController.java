package com.scentiva.modules.inventory.controller;

import com.scentiva.common.response.ApiResponse;
import com.scentiva.modules.inventory.dto.*;
import com.scentiva.modules.inventory.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
@Tag(name = "Inventory & Stock Engine", description = "Multi-Location Stock Reserves, Audits & Concurrency Control")
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/variant/{variantId}")
    @Operation(summary = "Get inventory breakdown for variant", description = "Returns stock levels across all active warehouses.")
    public ResponseEntity<ApiResponse<List<InventoryRecordResponse>>> getInventoryByVariant(@PathVariable Long variantId) {
        List<InventoryRecordResponse> records = inventoryService.getInventoryByVariant(variantId);
        return ResponseEntity.ok(ApiResponse.ok(records, "Inventory records retrieved"));
    }

    @GetMapping("/variant/{variantId}/available")
    @Operation(summary = "Get total available stock count", description = "Returns aggregate available stock across all fulfillment centers.")
    public ResponseEntity<ApiResponse<Integer>> getTotalAvailableStock(@PathVariable Long variantId) {
        int available = inventoryService.getTotalAvailableStock(variantId);
        return ResponseEntity.ok(ApiResponse.ok(available, "Total available stock retrieved"));
    }

    @GetMapping("/low-stock")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'INVENTORY_MANAGER')")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Get low stock alerts (Admin)", description = "Returns all variants at or below their low-stock threshold.")
    public ResponseEntity<ApiResponse<List<InventoryRecordResponse>>> getLowStockAlerts() {
        List<InventoryRecordResponse> alerts = inventoryService.getLowStockAlerts();
        return ResponseEntity.ok(ApiResponse.ok(alerts, "Low stock alerts retrieved"));
    }

    @PostMapping("/adjust")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'INVENTORY_MANAGER')")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Adjust inventory stock (Admin)", description = "Adjusts stock with reason code and records immutable ledger movement.")
    public ResponseEntity<ApiResponse<InventoryRecordResponse>> adjustStock(@Valid @RequestBody InventoryAdjustRequest request) {
        InventoryRecordResponse response = inventoryService.adjustStock(request);
        return ResponseEntity.ok(ApiResponse.ok(response, "Stock adjusted successfully"));
    }

    @PostMapping("/reserve")
    @Operation(summary = "Reserve stock for checkout", description = "Pessimistically locks and holds stock for 15 minutes during checkout.")
    public ResponseEntity<ApiResponse<StockReservationResponse>> reserveStock(@Valid @RequestBody StockReservationRequest request) {
        StockReservationResponse response = inventoryService.reserveStock(request);
        return ResponseEntity.ok(ApiResponse.ok(response, "Stock reserved successfully"));
    }

    @PostMapping("/release")
    @Operation(summary = "Release reserved stock", description = "Releases held stock upon checkout cancellation or expiration.")
    public ResponseEntity<ApiResponse<Void>> releaseStock(
            @RequestParam Long variantId,
            @RequestParam Long warehouseId,
            @RequestParam int quantity,
            @RequestParam String referenceId) {
        inventoryService.releaseStockReservation(variantId, warehouseId, quantity, referenceId);
        return ResponseEntity.ok(ApiResponse.ok(null, "Stock reservation released"));
    }

    @GetMapping("/movements/variant/{variantId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'INVENTORY_MANAGER')")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Get inventory audit movements (Admin)", description = "Returns immutable chronological movement ledger for audit.")
    public ResponseEntity<ApiResponse<List<InventoryMovementResponse>>> getMovementsByVariant(@PathVariable Long variantId) {
        List<InventoryMovementResponse> movements = inventoryService.getMovementsByVariant(variantId);
        return ResponseEntity.ok(ApiResponse.ok(movements, "Inventory movements retrieved"));
    }
}
