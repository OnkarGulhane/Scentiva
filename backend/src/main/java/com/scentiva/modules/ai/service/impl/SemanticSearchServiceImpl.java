package com.scentiva.modules.ai.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scentiva.modules.ai.client.PythonAiServiceClient;
import com.scentiva.modules.ai.dto.SemanticSearchRequest;
import com.scentiva.modules.ai.dto.SemanticSearchResponse;
import com.scentiva.modules.ai.provider.AiProvider;
import com.scentiva.modules.ai.service.SemanticSearchService;
import com.scentiva.modules.catalog.model.Product;
import com.scentiva.modules.catalog.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SemanticSearchServiceImpl implements SemanticSearchService {

    private final ProductRepository productRepository;
    private final AiProvider aiProvider;
    private final PythonAiServiceClient pythonAiClient;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional(readOnly = true)
    public SemanticSearchResponse search(SemanticSearchRequest request) {
        log.info("Performing semantic search for query: {}", request.getQuery());

        // Try Python AI service
        Map<String, Object> payload = new HashMap<>();
        payload.put("query", request.getQuery());
        payload.put("limit", request.getLimit() > 0 ? request.getLimit() : 10);

        Optional<JsonNode> pythonResponse = pythonAiClient.postForJson("/api/v1/ai/search/semantic", payload);
        if (pythonResponse.isPresent()) {
            try {
                return objectMapper.treeToValue(pythonResponse.get(), SemanticSearchResponse.class);
            } catch (Exception e) {
                log.warn("Failed to parse Python semantic search response, using fallback: {}", e.getMessage());
            }
        }

        // Fallback: In-JVM semantic analysis
        List<Product> products = productRepository.findAll().stream()
                .filter(p -> p.isActive() && !p.isDeleted())
                .toList();

        return aiProvider.performSemanticSearch(request.getQuery(), request.getLimit(), products);
    }
}

