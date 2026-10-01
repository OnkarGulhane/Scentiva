package com.scentiva.modules.catalog.dto;

import com.scentiva.modules.catalog.model.BrandTier;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Brand details response DTO.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Brand / Maison Details Response")
public class BrandResponse {

    private Long id;
    private String name;
    private String slug;
    private String originCountry;
    private String description;
    private String logoUrl;
    private String coverImageUrl;
    private BrandTier tier;
    private boolean isActive;
}
