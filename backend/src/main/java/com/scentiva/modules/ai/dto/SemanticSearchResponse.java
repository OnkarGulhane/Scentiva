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
public class SemanticSearchResponse {
    private String query;
    private String interpretedIntent;
    private List<String> detectedNotes;
    private List<String> detectedEmotions;
    private List<SemanticProductMatchDto> results;
}
