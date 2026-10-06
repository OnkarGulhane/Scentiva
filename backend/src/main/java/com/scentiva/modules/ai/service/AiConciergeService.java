package com.scentiva.modules.ai.service;

import com.scentiva.modules.ai.dto.*;

import java.util.List;

public interface AiConciergeService {

    AiAssistantChatResponse assistantChat(AiAssistantChatRequest request);

    ScentQuizResponse findScent(ScentQuizRequest request);

    AiRecommendationResponse getPersonalizedRecommendations(String userId, List<String> viewedIds, List<String> cartIds, List<String> wishlistIds, List<String> brands, List<String> families, int limit);

    AiSupportChatResponse supportChat(AiSupportChatRequest request);

    AiChatResponse chatWithConcierge(AiChatRequest request);

    ProductEditorialDescriptionResponse getProductEditorialDescription(Long productId);

    ProductSentimentSummaryResponse getProductSentimentSummary(Long productId);
}

