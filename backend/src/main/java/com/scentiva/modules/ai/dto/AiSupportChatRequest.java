package com.scentiva.modules.ai.dto;

import jakarta.validation.constraints.NotBlank;
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
public class AiSupportChatRequest {
    @NotBlank(message = "Message cannot be empty")
    private String message;

    private String conversationId;
    private String userId;
    private String authToken;
}
