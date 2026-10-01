package com.scentiva.modules.ai.service.impl;

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

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SemanticSearchServiceImpl implements SemanticSearchService {

    private final ProductRepository productRepository;
    private final AiProvider aiProvider;

    @Override
    @Transactional(readOnly = true)
    public SemanticSearchResponse search(SemanticSearchRequest request) {
        List<Product> products = productRepository.findAll().stream()
                .filter(p -> p.isActive() && !p.isDeleted())
                .toList();

        return aiProvider.performSemanticSearch(request.getQuery(), request.getLimit(), products);
    }
}
