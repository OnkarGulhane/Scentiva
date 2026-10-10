package com.scentiva.modules.shipping.dto.shiprocket;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShiprocketOrderItem {
    private String name;
    private String sku;
    private int units;
    @JsonProperty("selling_price")
    private String sellingPrice;
    @Builder.Default
    private String discount = "0";
    @Builder.Default
    private String tax = "0";
}
