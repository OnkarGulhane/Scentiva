package com.scentiva.modules.ai.service;

import com.scentiva.modules.ai.dto.*;

public interface AiConciergeService {

    ScentQuizResponse findScent(ScentQuizRequest request);

    AiChatResponse chatWithConcierge(AiChatRequest request);

    ProductEditorialDescriptionResponse getProductEditorialDescription(Long productId);

    ProductSentimentSummaryResponse getProductSentimentSummary(Long productId);
}
