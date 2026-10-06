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
public class AiSupportChatResponse {
    private boolean success;
    private String category;
    private String message;
    private Map<String, Object> orderInfo;
    private List<Map<String, Object>> suggestedActions;
    private List<String> sources;
}
