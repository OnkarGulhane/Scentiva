package com.scentiva.modules.ai.provider;

import com.scentiva.modules.ai.dto.*;
import com.scentiva.modules.catalog.model.Product;
import com.scentiva.modules.review.model.Review;

import java.util.List;

public interface AiProvider {

    ScentQuizResponse evaluateScentQuiz(ScentQuizRequest request, List<Product> availableProducts);

    SemanticSearchResponse performSemanticSearch(String query, int limit, List<Product> availableProducts);

    AiChatResponse generateChatResponse(AiChatRequest request, List<Product> availableProducts);

    ProductEditorialDescriptionResponse generateEditorialStory(Product product);

    ProductSentimentSummaryResponse summarizeSentiment(Long productId, List<Review> reviews);
}
