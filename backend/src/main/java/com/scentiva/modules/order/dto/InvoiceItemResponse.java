package com.scentiva.modules.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceItemResponse {

    @Schema(description = "Line item ID", example = "101")
    private Long id;

    @Schema(description = "Product Variant SKU", example = "MFK-BR540-EDP-70ML")
    private String sku;

    @Schema(description = "Product Name", example = "Baccarat Rouge 540")
    private String productName;

    @Schema(description = "Brand Name", example = "Maison Francis Kurkdjian")
    private String brandName;

    @Schema(description = "Volume in ML", example = "70")
    private Integer volumeMl;

    @Schema(description = "Concentration", example = "Extrait de Parfum")
    private String concentration;

    @Schema(description = "Quantity purchased", example = "1")
    private Integer quantity;

    @Schema(description = "Unit price in INR", example = "18500.00")
    private BigDecimal unitPrice;

    @Schema(description = "Line item total amount in INR", example = "18500.00")
    private BigDecimal totalPrice;
}
