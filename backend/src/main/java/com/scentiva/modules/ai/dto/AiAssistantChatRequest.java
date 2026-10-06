package com.scentiva.modules.ai.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiAssistantChatRequest {
    @NotBlank(message = "Query cannot be empty")
    private String query;

    private String conversationId;
    private String userId;
    private Map<String, Object> userContext;
    private Map<String, Object> filters;
}
