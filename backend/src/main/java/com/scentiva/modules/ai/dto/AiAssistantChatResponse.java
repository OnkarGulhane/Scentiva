package com.scentiva.modules.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiAssistantChatResponse {
    private boolean success;
    private String intent;
    private String message;
    private List<Map<String, Object>> recommendedProducts;
    private Map<String, Object> extractedCriteria;
    private List<Map<String, Object>> actions;
    private List<String> suggestedFollowups;
}
