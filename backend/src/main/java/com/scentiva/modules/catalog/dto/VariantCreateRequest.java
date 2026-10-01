package com.scentiva.modules.catalog.dto;

import com.scentiva.modules.catalog.model.Concentration;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Variant Creation Payload")
public class VariantCreateRequest {

    @NotBlank(message = "SKU is required")
    private String sku;

    @NotNull(message = "Volume in ML is required")
    private Integer volumeMl;

    @Builder.Default
    private Concentration concentration = Concentration.EDP;

    @NotNull(message = "Base price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Base price must be greater than 0")
    private BigDecimal basePrice;

    private BigDecimal salePrice;

    @Builder.Default
    private Integer weightGrams = 350;

    @Builder.Default
    private boolean isActive = true;
}
