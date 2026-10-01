package com.scentiva.modules.inventory.controller;

import com.scentiva.common.response.ApiResponse;
import com.scentiva.modules.inventory.dto.WarehouseCreateRequest;
import com.scentiva.modules.inventory.dto.WarehouseResponse;
import com.scentiva.modules.inventory.service.WarehouseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/warehouses")
@RequiredArgsConstructor
@Tag(name = "Warehouse Operations", description = "Fulfillment Centers & Multi-Location Warehouse Management")
public class WarehouseController {

    private final WarehouseService warehouseService;

    @GetMapping
    @Operation(summary = "Get active fulfillment centers", description = "Returns all active warehouses for order routing.")
    public ResponseEntity<ApiResponse<List<WarehouseResponse>>> getActiveWarehouses() {
        List<WarehouseResponse> warehouses = warehouseService.getActiveWarehouses();
        return ResponseEntity.ok(ApiResponse.ok(warehouses, "Active warehouses retrieved"));
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'INVENTORY_MANAGER')")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Get all warehouses (Admin)", description = "Returns all warehouses including inactive.")
    public ResponseEntity<ApiResponse<List<WarehouseResponse>>> getAllWarehouses() {
        List<WarehouseResponse> warehouses = warehouseService.getAllWarehouses();
        return ResponseEntity.ok(ApiResponse.ok(warehouses, "All warehouses retrieved"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get warehouse by ID", description = "Returns details of a specific fulfillment center.")
    public ResponseEntity<ApiResponse<WarehouseResponse>> getWarehouseById(@PathVariable Long id) {
        WarehouseResponse warehouse = warehouseService.getWarehouseById(id);
        return ResponseEntity.ok(ApiResponse.ok(warehouse, "Warehouse retrieved successfully"));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Create fulfillment center (Admin)", description = "Registers a new warehouse location.")
    public ResponseEntity<ApiResponse<WarehouseResponse>> createWarehouse(@Valid @RequestBody WarehouseCreateRequest request) {
        WarehouseResponse warehouse = warehouseService.createWarehouse(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(warehouse, "Warehouse created successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Update fulfillment center (Admin)", description = "Updates warehouse location or active status.")
    public ResponseEntity<ApiResponse<WarehouseResponse>> updateWarehouse(
            @PathVariable Long id,
            @Valid @RequestBody WarehouseCreateRequest request) {
        WarehouseResponse warehouse = warehouseService.updateWarehouse(id, request);
        return ResponseEntity.ok(ApiResponse.ok(warehouse, "Warehouse updated successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Delete warehouse (Admin)", description = "Soft-deletes a warehouse.")
    public ResponseEntity<ApiResponse<Void>> deleteWarehouse(@PathVariable Long id) {
        warehouseService.deleteWarehouse(id);
        return ResponseEntity.ok(ApiResponse.ok(null, "Warehouse deleted successfully"));
    }
}
