package com.scentiva.modules.catalog.dto;

import com.scentiva.modules.catalog.model.Concentration;
import com.scentiva.modules.catalog.model.GenderTarget;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Lightweight Product Summary DTO for Catalog Listing & Search.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Product Summary for Storefront Catalog Card")
public class ProductSummaryResponse {

    private Long id;
    private String name;
    private String slug;
    private String brandName;
    private String brandSlug;
    private String categoryName;
    private String categorySlug;
    private GenderTarget gender;
    private String fragranceFamily;
    private Concentration primaryConcentration;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private String primaryImageUrl;
    private boolean isFeatured;
    private boolean inStock;
    private int variantCount;
}
