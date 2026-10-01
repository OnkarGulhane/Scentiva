package com.scentiva.modules.ai.service;

import com.scentiva.modules.ai.dto.SemanticSearchRequest;
import com.scentiva.modules.ai.dto.SemanticSearchResponse;
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

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class SemanticSearchServiceTest {

    @Autowired
    private SemanticSearchService semanticSearchService;

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

    @BeforeEach
    void setUp() {
        Brand brand = brandRepository.findBySlugAndIsDeletedFalse("tom-ford-ai")
                .orElseGet(() -> brandRepository.save(Brand.builder()
                        .name("Tom Ford")
                        .slug("tom-ford-ai")
                        .originCountry("United States")
                        .tier(BrandTier.PRESTIGE)
                        .build()));

        Category category = categoryRepository.findBySlugAndIsDeletedFalse("oriental-ai")
                .orElseGet(() -> categoryRepository.save(Category.builder()
                        .name("Oriental")
                        .slug("oriental-ai")
                        .build()));

        Product product = productRepository.findBySlugAndIsDeletedFalse("tobacco-vanille-ai")
                .orElseGet(() -> productRepository.save(Product.builder()
                        .brand(brand)
                        .category(category)
                        .name("Tobacco Vanille")
                        .slug("tobacco-vanille-ai")
                        .gender(GenderTarget.UNISEX)
                        .isActive(true)
                        .build()));

        if (product.getOlfactoryPyramid() == null) {
            OlfactoryPyramid pyramid = olfactoryPyramidRepository.save(OlfactoryPyramid.builder()
                    .product(product)
                    .topNotes("Tobacco Leaf, Spicy Notes")
                    .heartNotes("Tonka Bean, Tobacco Blossom, Vanilla, Cacao")
                    .baseNotes("Dried Fruits, Woody Notes")
                    .fragranceFamily("Warm Oriental")
                    .build());
            product.setOlfactoryPyramid(pyramid);
        }

        productVariantRepository.findBySkuAndIsDeletedFalse("TF-TOB-100-AI")
                .orElseGet(() -> productVariantRepository.save(ProductVariant.builder()
                        .product(product)
                        .sku("TF-TOB-100-AI")
                        .volumeMl(100)
                        .concentration(Concentration.EDP)
                        .basePrice(new BigDecimal("31000.00"))
                        .isActive(true)
                        .build()));
    }

    @Test
    @DisplayName("Should perform natural language semantic search for warm vanilla perfumes")
    void shouldPerformSemanticSearch() {
        SemanticSearchRequest request = SemanticSearchRequest.builder()
                .query("warm spicy vanilla and tobacco for winter evenings")
                .limit(5)
                .build();

        SemanticSearchResponse response = semanticSearchService.search(request);

        assertThat(response).isNotNull();
        assertThat(response.getDetectedNotes()).contains("vanilla", "tobacco");
        assertThat(response.getDetectedEmotions()).contains("warm", "spicy");
        assertThat(response.getResults()).isNotEmpty();
        assertThat(response.getResults().get(0).getProduct().getName()).isEqualTo("Tobacco Vanille");
    }
}
