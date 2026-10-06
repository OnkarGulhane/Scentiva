package com.scentiva.modules.ai.controller;

import com.scentiva.common.response.ApiResponse;
import com.scentiva.modules.ai.dto.*;
import com.scentiva.modules.ai.service.AiConciergeService;
import com.scentiva.modules.ai.service.SemanticSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
@Tag(name = "AI Concierge & Semantic Search", description = "Olfactory quiz matchmaking, natural language semantic search, conversational concierge and sentiment analytics")
public class AiConciergeController {

    private final AiConciergeService aiConciergeService;
    private final SemanticSearchService semanticSearchService;

    @PostMapping("/scent-finder")
    @Operation(summary = "Scent Finder Quiz Matcher", description = "Computes bespoke fragrance recommendations based on user personality, intensity, and preferred notes.")
    public ResponseEntity<ApiResponse<ScentQuizResponse>> findScent(@RequestBody ScentQuizRequest request) {
        ScentQuizResponse response = aiConciergeService.findScent(request);
        return ResponseEntity.ok(ApiResponse.ok(response, "Recommendations generated successfully"));
    }

    @GetMapping("/semantic-search")
    @Operation(summary = "Semantic Search (GET)", description = "Searches catalog using natural language scent descriptions.")
    public ResponseEntity<ApiResponse<SemanticSearchResponse>> semanticSearchGet(
            @RequestParam String q,
            @RequestParam(defaultValue = "10") int limit) {
        SemanticSearchRequest request = SemanticSearchRequest.builder().query(q).limit(limit).build();
        SemanticSearchResponse response = semanticSearchService.search(request);
        return ResponseEntity.ok(ApiResponse.ok(response, "Semantic search results"));
    }

    @PostMapping("/semantic-search")
    @Operation(summary = "Semantic Search (POST)", description = "Searches catalog using complex natural language search queries.")
    public ResponseEntity<ApiResponse<SemanticSearchResponse>> semanticSearchPost(
            @Valid @RequestBody SemanticSearchRequest request) {
        SemanticSearchResponse response = semanticSearchService.search(request);
        return ResponseEntity.ok(ApiResponse.ok(response, "Semantic search results"));
    }

    @PostMapping("/assistant/chat")
    @Operation(summary = "AI Shopping Assistant", description = "Understands natural language shopping queries, extracts olfactory constraints, and returns verified real catalog recommendations.")
    public ResponseEntity<ApiResponse<AiAssistantChatResponse>> assistantChat(@Valid @RequestBody AiAssistantChatRequest request) {
        AiAssistantChatResponse response = aiConciergeService.assistantChat(request);
        return ResponseEntity.ok(ApiResponse.ok(response, "Assistant response generated"));
    }

    @GetMapping("/recommendations")
    @Operation(summary = "Personalized AI Recommendations", description = "Generates recommendations based on user history, viewed items, wishlist, and olfactory preferences.")
    public ResponseEntity<ApiResponse<AiRecommendationResponse>> getRecommendations(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) java.util.List<String> viewed,
            @RequestParam(required = false) java.util.List<String> cart,
            @RequestParam(required = false) java.util.List<String> wishlist,
            @RequestParam(required = false) java.util.List<String> brands,
            @RequestParam(required = false) java.util.List<String> families,
            @RequestParam(defaultValue = "4") int limit) {
        AiRecommendationResponse response = aiConciergeService.getPersonalizedRecommendations(
                userId, viewed, cart, wishlist, brands, families, limit);
        return ResponseEntity.ok(ApiResponse.ok(response, "Recommendations retrieved"));
    }

    @PostMapping("/support/chat")
    @Operation(summary = "AI Customer Support Chat", description = "Customer service assistant answering policy, shipping, return and live authenticated order tracking inquiries.")
    public ResponseEntity<ApiResponse<AiSupportChatResponse>> supportChat(@Valid @RequestBody AiSupportChatRequest request) {
        AiSupportChatResponse response = aiConciergeService.supportChat(request);
        return ResponseEntity.ok(ApiResponse.ok(response, "Support response generated"));
    }

    @PostMapping("/concierge/chat")
    @Operation(summary = "AI Fragrance Concierge Chat", description = "Conversational perfumery assistance, advice and fragrance discovery.")
    public ResponseEntity<ApiResponse<AiChatResponse>> chat(@Valid @RequestBody AiChatRequest request) {
        AiChatResponse response = aiConciergeService.chatWithConcierge(request);
        return ResponseEntity.ok(ApiResponse.ok(response, "Concierge response generated"));
    }

    @GetMapping("/product/{productId}/editorial-description")
    @Operation(summary = "AI Editorial Description", description = "Generates high-fashion editorial story & olfactory narrative for a perfume flacon.")
    public ResponseEntity<ApiResponse<ProductEditorialDescriptionResponse>> getEditorialDescription(
            @PathVariable Long productId) {
        ProductEditorialDescriptionResponse response = aiConciergeService.getProductEditorialDescription(productId);
        return ResponseEntity.ok(ApiResponse.ok(response, "Editorial narrative retrieved"));
    }

    @GetMapping("/product/{productId}/sentiment-summary")
    @Operation(summary = "AI Review Sentiment Analysis", description = "Summarizes collector sentiment and highlights praised nuances.")
    public ResponseEntity<ApiResponse<ProductSentimentSummaryResponse>> getSentimentSummary(
            @PathVariable Long productId) {
        ProductSentimentSummaryResponse response = aiConciergeService.getProductSentimentSummary(productId);
        return ResponseEntity.ok(ApiResponse.ok(response, "Sentiment analysis summarized"));
    }
}

