package com.scentiva.modules.ai.service;

import com.scentiva.modules.ai.dto.*;
import com.scentiva.modules.catalog.model.*;
import com.scentiva.modules.catalog.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AiConciergeServiceTest {

    @Autowired
    private AiConciergeService aiConciergeService;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductVariantRepository productVariantRepository;

    @Autowired
    private OlfactoryPyramidRepository olfactoryPyramidRepository;

    private Product product;

    @BeforeEach
    void setUp() {
        Brand brand = brandRepository.findBySlugAndIsDeletedFalse("creed-ai")
                .orElseGet(() -> brandRepository.save(Brand.builder()
                        .name("Creed")
                        .slug("creed-ai")
                        .originCountry("France")
                        .tier(BrandTier.HERITAGE_MAISON)
                        .build()));

        Category category = categoryRepository.findBySlugAndIsDeletedFalse("woody-ai")
                .orElseGet(() -> categoryRepository.save(Category.builder()
                        .name("Woody")
                        .slug("woody-ai")
                        .build()));

        product = productRepository.findBySlugAndIsDeletedFalse("aventus-ai")
                .orElseGet(() -> productRepository.save(Product.builder()
                        .brand(brand)
                        .category(category)
                        .name("Aventus")
                        .slug("aventus-ai")
                        .gender(GenderTarget.FOR_HIM)
                        .isActive(true)
                        .build()));

        if (product.getOlfactoryPyramid() == null) {
            OlfactoryPyramid pyramid = olfactoryPyramidRepository.save(OlfactoryPyramid.builder()
                    .product(product)
                    .topNotes("Pineapple, Bergamot, Blackcurrant")
                    .heartNotes("Birch, Patchouli, Jasmine")
                    .baseNotes("Musk, Oakmoss, Ambergris, Vanilla")
                    .fragranceFamily("Woody Citrus")
                    .build());
            product.setOlfactoryPyramid(pyramid);
        }

        productVariantRepository.findBySkuAndIsDeletedFalse("CRD-AVN-100-AI")
                .orElseGet(() -> productVariantRepository.save(ProductVariant.builder()
                        .product(product)
                        .sku("CRD-AVN-100-AI")
                        .volumeMl(100)
                        .concentration(Concentration.EDP)
                        .basePrice(new BigDecimal("36000.00"))
                        .isActive(true)
                        .build()));
    }

    @Test
    @DisplayName("Should evaluate scent finder quiz and return matching recommendations")
    void shouldFindScentByQuiz() {
        ScentQuizRequest request = ScentQuizRequest.builder()
                .fragranceFamily("Woody")
                .occasion("Date Night")
                .intensity("Bold & Intense")
                .gender(GenderTarget.FOR_HIM)
                .preferredNotes(List.of("Bergamot", "Birch", "Vanilla"))
                .build();

        ScentQuizResponse response = aiConciergeService.findScent(request);

        assertThat(response).isNotNull();
        assertThat(response.getPersonaTitle()).isNotEmpty();
        assertThat(response.getRecommendations()).isNotEmpty();
        assertThat(response.getRecommendations().get(0).getMatchScore()).isGreaterThanOrEqualTo(70);
    }

    @Test
    @DisplayName("Should respond to AI concierge conversational queries")
    void shouldChatWithConcierge() {
        AiChatRequest request = AiChatRequest.builder()
                .message("Looking for a signature perfume with bergamot and vanilla")
                .build();

        AiChatResponse response = aiConciergeService.chatWithConcierge(request);

        assertThat(response).isNotNull();
        assertThat(response.getReply()).contains("Scentiva Haute Parfumerie Concierge");
        assertThat(response.getSuggestedFragrances()).isNotEmpty();
    }

    @Test
    @DisplayName("Should generate rich editorial story for product")
    void shouldGenerateEditorialDescription() {
        ProductEditorialDescriptionResponse story = aiConciergeService.getProductEditorialDescription(product.getId());

        assertThat(story).isNotNull();
        assertThat(story.getProductName()).isEqualTo("Aventus");
        assertThat(story.getBrandName()).isEqualTo("Creed");
        assertThat(story.getOlfactoryNarrative()).contains("Creed");
    }

    @Test
    @DisplayName("Should generate sentiment summary for product")
    void shouldGetSentimentSummary() {
        ProductSentimentSummaryResponse sentiment = aiConciergeService.getProductSentimentSummary(product.getId());

        assertThat(sentiment).isNotNull();
        assertThat(sentiment.getSentimentClassification()).isNotEmpty();
        assertThat(sentiment.getSyntheticEditorialConsensus()).isNotEmpty();
    }
}
