package com.scentiva.modules.catalog.dto;

import com.scentiva.modules.catalog.model.GenderTarget;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Comprehensive Product Detail Page (PDP) Response DTO.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Complete Product Detail Response")
public class ProductDetailResponse {

    private Long id;
    private String name;
    private String slug;
    private String description;
    private GenderTarget gender;
    private boolean isFeatured;
    private boolean isActive;

    private BrandResponse brand;
    private CategoryResponse category;
    private OlfactoryPyramidDto olfactoryPyramid;
    private List<ProductVariantDto> variants;
    private List<ProductImageDto> images;
}
