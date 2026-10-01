package com.scentiva.modules.cart.dto;

import com.scentiva.modules.catalog.model.Concentration;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponse {
    private Long id;
    private Long variantId;
    private String sku;
    private Long productId;
    private String productName;
    private String productSlug;
    private String brandName;
    private Concentration concentration;
    private Integer volumeMl;
    private String imageUrl;
    private BigDecimal unitPrice;
    private int quantity;
    private BigDecimal itemTotal;
    private boolean isAvailable;
    private int availableStock;
}
