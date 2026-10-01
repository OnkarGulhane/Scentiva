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
public class AiChatResponse {
    private String reply;
    private List<ProductSummaryResponse> suggestedFragrances;
    private List<String> highlightedNotes;
    private List<String> suggestedQuestions;
}
