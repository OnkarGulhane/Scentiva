package com.scentiva.modules.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSentimentSummaryResponse {
    private Long productId;
    private String sentimentClassification; // "OVERWHELMINGLY_POSITIVE", "POSITIVE", "MIXED", "NEUTRAL"
    private double positivePercentage;
    private List<String> topPraisedAttributes;
    private List<String> topCritiques;
    private String syntheticEditorialConsensus;
}
