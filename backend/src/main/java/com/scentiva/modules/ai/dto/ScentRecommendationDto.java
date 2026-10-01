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
public class ScentRecommendationDto {
    private ProductSummaryResponse product;
    private int matchScore; // 0 - 100
    private String matchReason;
    private List<String> matchingNotes;
    private String idealOccasion;
}
