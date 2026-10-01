package com.scentiva.modules.catalog.dto;

import com.scentiva.modules.catalog.model.BrandTier;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Create or Update Brand Payload")
public class BrandCreateRequest {

    @NotBlank(message = "Brand name is required")
    @Size(max = 150, message = "Brand name cannot exceed 150 characters")
    private String name;

    @NotBlank(message = "Brand slug is required")
    @Size(max = 150, message = "Brand slug cannot exceed 150 characters")
    private String slug;

    @NotBlank(message = "Origin country is required")
    @Size(max = 100, message = "Origin country cannot exceed 100 characters")
    private String originCountry;

    private String description;
    private String logoUrl;
    private String coverImageUrl;

    @Builder.Default
    private BrandTier tier = BrandTier.PRESTIGE;

    @Builder.Default
    private boolean isActive = true;
}
