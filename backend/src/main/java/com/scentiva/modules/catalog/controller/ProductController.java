package com.scentiva.modules.catalog.controller;

import com.scentiva.common.response.ApiPaginatedResponse;
import com.scentiva.common.response.ApiResponse;
import com.scentiva.modules.catalog.dto.ProductCreateRequest;
import com.scentiva.modules.catalog.dto.ProductDetailResponse;
import com.scentiva.modules.catalog.dto.ProductSummaryResponse;
import com.scentiva.modules.catalog.model.GenderTarget;
import com.scentiva.modules.catalog.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Products & Catalog Discovery", description = "Fragrance Catalog, Olfactory Pyramids, Variants & Faceted Search")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @Operation(summary = "Get paginated products with faceted filters", description = "Filter by brand, category, gender with pagination and sorting.")
    public ResponseEntity<ApiResponse<ApiPaginatedResponse<ProductSummaryResponse>>> getProducts(
            @RequestParam(required = false) Long brandId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) GenderTarget gender,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "id,desc") String sort) {

        String[] sortParts = sort.split(",");
        Sort.Direction direction = sortParts.length > 1 && sortParts[1].equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortParts[0]));

        ApiPaginatedResponse<ProductSummaryResponse> response = productService.getProducts(brandId, categoryId, gender, pageable);
        return ResponseEntity.ok(ApiResponse.ok(response, "Products retrieved successfully"));
    }

    @GetMapping("/featured")
    @Operation(summary = "Get featured fragrance products", description = "Returns top highlighted fragrances for home showcases.")
    public ResponseEntity<ApiResponse<List<ProductSummaryResponse>>> getFeaturedProducts() {
        List<ProductSummaryResponse> featured = productService.getFeaturedProducts();
        return ResponseEntity.ok(ApiResponse.ok(featured, "Featured products retrieved"));
    }

    @GetMapping("/search")
    @Operation(summary = "Search fragrance catalog", description = "Full-text search matching name, brand, or category.")
    public ResponseEntity<ApiResponse<ApiPaginatedResponse<ProductSummaryResponse>>> searchProducts(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        Pageable pageable = PageRequest.of(page, size);
        ApiPaginatedResponse<ProductSummaryResponse> response = productService.searchProducts(q, pageable);
        return ResponseEntity.ok(ApiResponse.ok(response, "Search results retrieved"));
    }

    @GetMapping("/{slug}")
    @Operation(summary = "Get complete product details by slug (PDP)", description = "Returns olfactory notes pyramid, variants, pricing, and images.")
    public ResponseEntity<ApiResponse<ProductDetailResponse>> getProductBySlug(@PathVariable String slug) {
        ProductDetailResponse product = productService.getProductBySlug(slug);
        return ResponseEntity.ok(ApiResponse.ok(product, "Product details retrieved"));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'PRODUCT_MANAGER')")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Create a new fragrance product (Admin)", description = "Adds a new fragrance with olfactory pyramid and variants.")
    public ResponseEntity<ApiResponse<ProductDetailResponse>> createProduct(@Valid @RequestBody ProductCreateRequest request) {
        ProductDetailResponse product = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(product, "Product created successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'PRODUCT_MANAGER')")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Update product details (Admin)", description = "Updates metadata, pyramid notes, or variants.")
    public ResponseEntity<ApiResponse<ProductDetailResponse>> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductCreateRequest request) {
        ProductDetailResponse product = productService.updateProduct(id, request);
        return ResponseEntity.ok(ApiResponse.ok(product, "Product updated successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Delete product (Admin)", description = "Soft-deletes a product from the catalog.")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(ApiResponse.ok(null, "Product deleted successfully"));
    }
}
