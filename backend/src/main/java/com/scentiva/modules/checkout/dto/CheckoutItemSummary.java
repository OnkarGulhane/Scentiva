package com.scentiva.modules.checkout.dto;

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
public class CheckoutItemSummary {
    private Long variantId;
    private String sku;
    private String productName;
    private String brandName;
    private Concentration concentration;
    private Integer volumeMl;
    private String imageUrl;
    private BigDecimal unitPrice;
    private int quantity;
    private BigDecimal totalPrice;
}
