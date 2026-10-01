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
public class ScentQuizResponse {
    private String personaTitle;
    private String personaDescription;
    private String recommendedFamily;
    private List<ScentRecommendationDto> recommendations;
}
