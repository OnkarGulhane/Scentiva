package com.scentiva.modules.ai.service.impl;

import com.scentiva.common.exception.ResourceNotFoundException;
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

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiConciergeServiceImpl implements AiConciergeService {

    private final ProductRepository productRepository;
    private final ReviewRepository reviewRepository;
    private final AiProvider aiProvider;

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
