package com.scentiva.modules.catalog.dto;

import com.scentiva.modules.catalog.model.Concentration;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Product Variant DTO with volume, exact pricing, and available stock.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Product Variant Details")
public class ProductVariantDto {

    private Long id;
    private String sku;
    private Integer volumeMl;
    private Concentration concentration;
    private BigDecimal basePrice;
    private BigDecimal salePrice;
    private BigDecimal effectivePrice;
    private Integer weightGrams;
    private boolean isActive;
    private int availableStock;
}
