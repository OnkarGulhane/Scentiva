package com.scentiva.modules.catalog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Product Image Reference")
public class ProductImageDto {

    private Long id;
    private Long variantId;
    private String imageUrl;
    private String altText;
    private boolean isPrimary;
    private int displayOrder;
}
