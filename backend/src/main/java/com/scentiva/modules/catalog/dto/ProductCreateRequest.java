package com.scentiva.modules.catalog.dto;

import com.scentiva.modules.catalog.model.GenderTarget;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Create Fragrance Product Payload")
public class ProductCreateRequest {

    @NotNull(message = "Brand ID is required")
    private Long brandId;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    @NotBlank(message = "Product name is required")
    @Size(max = 255, message = "Product name cannot exceed 255 characters")
    private String name;

    @NotBlank(message = "Product slug is required")
    @Size(max = 255, message = "Product slug cannot exceed 255 characters")
    private String slug;

    private String description;

    @Builder.Default
    private GenderTarget gender = GenderTarget.UNISEX;

    @Builder.Default
    private boolean isFeatured = false;

    @Builder.Default
    private boolean isActive = true;

    @NotNull(message = "Olfactory pyramid is required for fragrance products")
    @Valid
    private OlfactoryPyramidDto olfactoryPyramid;

    @NotEmpty(message = "At least one product variant is required")
    @Valid
    private List<VariantCreateRequest> variants;
}
