package com.scentiva.modules.catalog.controller;

import com.scentiva.common.response.ApiResponse;
import com.scentiva.modules.catalog.dto.BrandCreateRequest;
import com.scentiva.modules.catalog.dto.BrandResponse;
import com.scentiva.modules.catalog.service.BrandService;
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
@RequestMapping("/api/v1/brands")
@RequiredArgsConstructor
@Tag(name = "Brands & Maisons", description = "Brand Directory, Maison Details & Catalog Association")
public class BrandController {

    private final BrandService brandService;

    @GetMapping
    @Operation(summary = "Get all active brands", description = "Returns active luxury and niche fragrance brands.")
    public ResponseEntity<ApiResponse<List<BrandResponse>>> getAllBrands() {
        List<BrandResponse> brands = brandService.getAllActiveBrands();
        return ResponseEntity.ok(ApiResponse.ok(brands, "Active brands retrieved"));
    }

    @GetMapping("/{slug}")
    @Operation(summary = "Get brand details by slug", description = "Retrieves brand profile, origin, and cover assets.")
    public ResponseEntity<ApiResponse<BrandResponse>> getBrandBySlug(@PathVariable String slug) {
        BrandResponse brand = brandService.getBrandBySlug(slug);
        return ResponseEntity.ok(ApiResponse.ok(brand, "Brand details retrieved"));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'PRODUCT_MANAGER')")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Create a new fragrance brand (Admin)", description = "Adds a new luxury brand to the catalog registry.")
    public ResponseEntity<ApiResponse<BrandResponse>> createBrand(@Valid @RequestBody BrandCreateRequest request) {
        BrandResponse brand = brandService.createBrand(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(brand, "Brand created successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'PRODUCT_MANAGER')")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Update brand details (Admin)", description = "Updates brand metadata, logo, or tier.")
    public ResponseEntity<ApiResponse<BrandResponse>> updateBrand(@PathVariable Long id, @Valid @RequestBody BrandCreateRequest request) {
        BrandResponse brand = brandService.updateBrand(id, request);
        return ResponseEntity.ok(ApiResponse.ok(brand, "Brand updated successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Delete brand (Admin)", description = "Soft-deletes a brand from the catalog.")
    public ResponseEntity<ApiResponse<Void>> deleteBrand(@PathVariable Long id) {
        brandService.deleteBrand(id);
        return ResponseEntity.ok(ApiResponse.ok(null, "Brand deleted successfully"));
    }
}
