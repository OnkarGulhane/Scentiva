package com.scentiva.modules.returns.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReturnItemResponse {
    private Long id;
    private Long orderItemId;
    private Long variantId;
    private String sku;
    private String productName;
    private String variantTitle;
    private Integer quantity;
    private BigDecimal unitPrice;
    private String reason;
}
