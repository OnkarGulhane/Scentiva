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
public class ProductEditorialDescriptionResponse {
    private Long productId;
    private String productName;
    private String brandName;
    private String editorialHeadline;
    private String olfactoryNarrative;
    private String moodAndSillage;
    private List<String> recommendedPairings;
}
