package com.scentiva.modules.wishlist.dto;

import com.scentiva.modules.catalog.model.Concentration;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WishlistItemResponse {
    private Long id;
    private Long variantId;
    private String sku;
    private Long productId;
    private String productName;
    private String productSlug;
    private String brandName;
    private Concentration concentration;
    private Integer volumeMl;
    private BigDecimal basePrice;
    private BigDecimal salePrice;
    private BigDecimal effectivePrice;
    private String imageUrl;
    private boolean isInStock;
    private LocalDateTime addedAt;
}
