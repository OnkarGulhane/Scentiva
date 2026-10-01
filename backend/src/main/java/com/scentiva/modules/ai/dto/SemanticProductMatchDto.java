package com.scentiva.modules.ai.dto;

import com.scentiva.modules.catalog.dto.ProductSummaryResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SemanticProductMatchDto {
    private ProductSummaryResponse product;
    private double relevanceScore; // 0.0 - 1.0
    private String matchExplanation;
    private List<String> extractedAttributes;
}
