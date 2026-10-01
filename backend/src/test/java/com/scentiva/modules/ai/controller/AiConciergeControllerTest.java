package com.scentiva.modules.ai.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scentiva.modules.ai.dto.AiChatRequest;
import com.scentiva.modules.ai.dto.ScentQuizRequest;
import com.scentiva.modules.ai.dto.SemanticSearchRequest;
import com.scentiva.modules.catalog.model.*;
import com.scentiva.modules.catalog.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AiConciergeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductVariantRepository productVariantRepository;

    private Product product;

    @BeforeEach
    void setUp() {
        Brand brand = brandRepository.findBySlugAndIsDeletedFalse("dior-privee-ai")
                .orElseGet(() -> brandRepository.save(Brand.builder()
                        .name("Dior Privée")
                        .slug("dior-privee-ai")
                        .originCountry("France")
                        .tier(BrandTier.HERITAGE_MAISON)
                        .build()));

        Category category = categoryRepository.findBySlugAndIsDeletedFalse("floral-ai")
                .orElseGet(() -> categoryRepository.save(Category.builder()
                        .name("Floral")
                        .slug("floral-ai")
                        .build()));

        product = productRepository.findBySlugAndIsDeletedFalse("gris-dior-ai")
                .orElseGet(() -> productRepository.save(Product.builder()
                        .brand(brand)
                        .category(category)
                        .name("Gris Dior")
                        .slug("gris-dior-ai")
                        .gender(GenderTarget.UNISEX)
                        .isActive(true)
                        .build()));

        productVariantRepository.findBySkuAndIsDeletedFalse("DIO-GRI-125-AI")
                .orElseGet(() -> productVariantRepository.save(ProductVariant.builder()
                        .product(product)
                        .sku("DIO-GRI-125-AI")
                        .volumeMl(125)
                        .concentration(Concentration.EDP)
                        .basePrice(new BigDecimal("29500.00"))
                        .isActive(true)
                        .build()));
    }

    @Test
    @DisplayName("POST /api/v1/ai/scent-finder should return quiz recommendations anonymously")
    void shouldFindScentByQuiz() throws Exception {
        ScentQuizRequest request = ScentQuizRequest.builder()
                .fragranceFamily("Floral")
                .occasion("Evening Gala")
                .intensity("Balanced & Elegant")
                .gender(GenderTarget.UNISEX)
                .preferredNotes(List.of("Rose", "Oakmoss", "Patchouli"))
                .build();

        mockMvc.perform(post("/api/v1/ai/scent-finder")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.personaTitle").exists())
                .andExpect(jsonPath("$.data.recommendations").isArray());
    }

    @Test
    @DisplayName("GET /api/v1/ai/semantic-search should return matching perfumes")
    void shouldPerformSemanticSearchGet() throws Exception {
        mockMvc.perform(get("/api/v1/ai/semantic-search")
                        .param("q", "elegant floral rose fragrance")
                        .param("limit", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.results").isArray());
    }

    @Test
    @DisplayName("POST /api/v1/ai/semantic-search should process search request")
    void shouldPerformSemanticSearchPost() throws Exception {
        SemanticSearchRequest request = SemanticSearchRequest.builder()
                .query("mysterious chypre rose with mossy elegance")
                .limit(5)
                .build();

        mockMvc.perform(post("/api/v1/ai/semantic-search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.results").isArray());
    }

    @Test
    @DisplayName("POST /api/v1/ai/concierge/chat should return luxury perfumery answers")
    void shouldChatWithConcierge() throws Exception {
        AiChatRequest request = AiChatRequest.builder()
                .message("What are the best unisex fragrances for spring?")
                .build();

        mockMvc.perform(post("/api/v1/ai/concierge/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.reply").exists());
    }

    @Test
    @DisplayName("GET /api/v1/ai/product/{productId}/editorial-description should return story")
    void shouldGetEditorialDescription() throws Exception {
        mockMvc.perform(get("/api/v1/ai/product/" + product.getId() + "/editorial-description"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.productName", is("Gris Dior")));
    }

    @Test
    @DisplayName("GET /api/v1/ai/product/{productId}/sentiment-summary should return sentiment analysis")
    void shouldGetSentimentSummary() throws Exception {
        mockMvc.perform(get("/api/v1/ai/product/" + product.getId() + "/sentiment-summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.sentimentClassification").exists());
    }
}
