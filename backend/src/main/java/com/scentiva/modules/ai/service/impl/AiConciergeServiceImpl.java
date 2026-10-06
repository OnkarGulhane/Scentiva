package com.scentiva.modules.ai.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scentiva.common.exception.ResourceNotFoundException;
import com.scentiva.modules.ai.client.PythonAiServiceClient;
import com.scentiva.modules.ai.dto.*;
import com.scentiva.modules.ai.provider.AiProvider;
import com.scentiva.modules.ai.service.AiConciergeService;
import com.scentiva.modules.catalog.model.Product;
import com.scentiva.modules.catalog.repository.ProductRepository;
import com.scentiva.modules.review.model.Review;
import com.scentiva.modules.review.model.ReviewStatus;
import com.scentiva.modules.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiConciergeServiceImpl implements AiConciergeService {

    private final ProductRepository productRepository;
    private final ReviewRepository reviewRepository;
    private final AiProvider aiProvider;
    private final PythonAiServiceClient pythonAiClient;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional(readOnly = true)
    public AiAssistantChatResponse assistantChat(AiAssistantChatRequest request) {
        log.info("Processing AI Assistant chat query: {}", request.getQuery());
        
        // Try Python LangChain AI Service first
        Map<String, Object> payload = new HashMap<>();
        payload.put("query", request.getQuery());
        payload.put("conversation_id", request.getConversationId());
        payload.put("user_id", request.getUserId());
        payload.put("user_context", request.getUserContext() != null ? request.getUserContext() : Map.of());
        payload.put("filters", request.getFilters() != null ? request.getFilters() : Map.of());

        Optional<JsonNode> pythonResponse = pythonAiClient.postForJson("/api/v1/ai/assistant/chat", payload);
        if (pythonResponse.isPresent()) {
            try {
                return objectMapper.treeToValue(pythonResponse.get(), AiAssistantChatResponse.class);
            } catch (Exception e) {
                log.warn("Failed to parse Python AI assistant response, falling back: {}", e.getMessage());
            }
        }

        // Fallback: Use catalog-grounded heuristic response
        List<Product> products = productRepository.findAll().stream()
                .filter(p -> p.isActive() && !p.isDeleted())
                .toList();
        
        AiChatResponse fallbackChat = aiProvider.generateChatResponse(
                AiChatRequest.builder().message(request.getQuery()).build(), products);

        List<Map<String, Object>> recProducts = fallbackChat.getSuggestedFragrances().stream()
                .map(p -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", p.getId());
                    map.put("productId", p.getId());
                    map.put("name", p.getName());
                    map.put("brandName", p.getBrandName());
                    map.put("price", p.getMinPrice());
                    map.put("effectivePrice", p.getMinPrice());
                    map.put("inStock", p.isInStock());
                    map.put("reason", "Curated haute parfumerie match for your olfactory preferences.");
                    map.put("primaryImageUrl", p.getPrimaryImageUrl());
                    map.put("slug", p.getSlug());
                    return map;
                }).toList();

        List<Map<String, Object>> actions = recProducts.stream()
                .limit(2)
                .map(p -> Map.<String, Object>of("type", "VIEW_PRODUCT", "productId", p.get("productId"), "slug", p.get("slug")))
                .toList();

        return AiAssistantChatResponse.builder()
                .success(true)
                .intent("product_search")
                .message(fallbackChat.getReply())
                .recommendedProducts(recProducts)
                .extractedCriteria(Map.of("source", "scentiva-catalog-fallback"))
                .actions(actions)
                .suggestedFollowups(fallbackChat.getSuggestedQuestions())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ScentQuizResponse findScent(ScentQuizRequest request) {
        List<Product> products = productRepository.findAll().stream()
                .filter(p -> p.isActive() && !p.isDeleted())
                .toList();

        return aiProvider.evaluateScentQuiz(request, products);
    }

    @Override
    @Transactional(readOnly = true)
    public AiRecommendationResponse getPersonalizedRecommendations(
            String userId, List<String> viewedIds, List<String> cartIds,
            List<String> wishlistIds, List<String> brands, List<String> families, int limit) {
        
        Map<String, Object> payload = new HashMap<>();
        payload.put("user_id", userId);
        payload.put("viewed_product_ids", viewedIds != null ? viewedIds : List.of());
        payload.put("cart_product_ids", cartIds != null ? cartIds : List.of());
        payload.put("wishlist_product_ids", wishlistIds != null ? wishlistIds : List.of());
        payload.put("favorite_brands", brands != null ? brands : List.of());
        payload.put("preferred_families", families != null ? families : List.of());
        payload.put("limit", limit > 0 ? limit : 4);

        Optional<JsonNode> pythonResponse = pythonAiClient.postForJson("/api/v1/ai/recommendations/personal", payload);
        if (pythonResponse.isPresent()) {
            try {
                return objectMapper.treeToValue(pythonResponse.get(), AiRecommendationResponse.class);
            } catch (Exception e) {
                log.warn("Failed to parse Python recommendations response: {}", e.getMessage());
            }
        }

        // Fallback recommendations from active catalog
        List<Product> activeProducts = productRepository.findAll().stream()
                .filter(p -> p.isActive() && !p.isDeleted())
                .limit(limit > 0 ? limit : 4)
                .toList();

        List<Map<String, Object>> recs = activeProducts.stream().map(p -> {
            Map<String, Object> map = new HashMap<>();
            map.put("productId", p.getId());
            map.put("name", p.getName());
            map.put("brand", p.getBrand().getName());
            map.put("family", p.getCategory().getName());
            map.put("slug", p.getSlug());
            map.put("reason", "Featured haute parfumerie signature composition.");
            return map;
        }).toList();

        return AiRecommendationResponse.builder()
                .success(true)
                .headline("Curated For Your Olfactory Profile")
                .explanation("Selected based on our prestigious catalog masterpieces.")
                .recommendations(recs)
                .build();
    }

    @Override
    public AiSupportChatResponse supportChat(AiSupportChatRequest request) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("message", request.getMessage());
        payload.put("conversation_id", request.getConversationId());
        payload.put("user_id", request.getUserId());
        payload.put("auth_token", request.getAuthToken());

        Optional<JsonNode> pythonResponse = pythonAiClient.postForJson("/api/v1/ai/support/chat", payload);
        if (pythonResponse.isPresent()) {
            try {
                return objectMapper.treeToValue(pythonResponse.get(), AiSupportChatResponse.class);
            } catch (Exception e) {
                log.warn("Failed to parse Python support chat response: {}", e.getMessage());
            }
        }

        // Offline / fallback support response
        return AiSupportChatResponse.builder()
                .success(true)
                .category("general")
                .message("Welcome to Scentiva Customer Concierge. All our perfumes are 100% authentic, shipped in temperature-controlled packaging, with 7-day hassle-free returns for unopened items.")
                .sources(List.of("Scentiva Official Policies"))
                .suggestedActions(List.of(
                        Map.of("label", "Track Order", "action", "NAVIGATE", "target", "/account/orders"),
                        Map.of("label", "Return Policy", "action", "NAVIGATE", "target", "/returns")
                ))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public AiChatResponse chatWithConcierge(AiChatRequest request) {
        List<Product> products = productRepository.findAll().stream()
                .filter(p -> p.isActive() && !p.isDeleted())
                .toList();

        return aiProvider.generateChatResponse(request, products);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductEditorialDescriptionResponse getProductEditorialDescription(Long productId) {
        Product product = productRepository.findById(productId)
                .filter(p -> !p.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        return aiProvider.generateEditorialStory(product);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductSentimentSummaryResponse getProductSentimentSummary(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product", "id", productId);
        }

        List<Review> reviews = reviewRepository.findByProductIdAndStatusAndIsDeletedFalse(
                productId, ReviewStatus.APPROVED, Pageable.unpaged()).getContent();

        return aiProvider.summarizeSentiment(productId, reviews);
    }
}

