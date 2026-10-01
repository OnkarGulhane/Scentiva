package com.scentiva.modules.seo.controller;

import com.scentiva.modules.catalog.model.*;
import com.scentiva.modules.catalog.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class SeoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductVariantRepository variantRepository;

    private Brand brand;
    private Product product;

    @BeforeEach
    void setUp() {
        brand = brandRepository.findBySlugAndIsDeletedFalse("creed-seo")
                .orElseGet(() -> brandRepository.save(Brand.builder()
                        .name("Creed")
                        .slug("creed-seo")
                        .originCountry("France")
                        .tier(BrandTier.HERITAGE_MAISON)
                        .description("Master perfumers since 1760.")
                        .build()));

        Category category = categoryRepository.findBySlugAndIsDeletedFalse("woody-seo")
                .orElseGet(() -> categoryRepository.save(Category.builder()
                        .name("Woody")
                        .slug("woody-seo")
                        .build()));

        product = productRepository.findBySlugAndIsDeletedFalse("aventus-seo")
                .orElseGet(() -> productRepository.save(Product.builder()
                        .brand(brand)
                        .category(category)
                        .name("Aventus")
                        .slug("aventus-seo")
                        .description("Sensual, audacious and contemporary scent.")
                        .gender(GenderTarget.FOR_HIM)
                        .isActive(true)
                        .build()));

        variantRepository.findBySkuAndIsDeletedFalse("CRE-AVE-100-SEO")
                .orElseGet(() -> variantRepository.save(ProductVariant.builder()
                        .product(product)
                        .sku("CRE-AVE-100-SEO")
                        .volumeMl(100)
                        .concentration(Concentration.EDP)
                        .basePrice(new BigDecimal("33000.00"))
                        .isActive(true)
                        .build()));
    }

    @Test
    @DisplayName("GET /api/v1/seo/product/{slug} should return product SEO metadata and JSON-LD")
    void shouldGetProductSeo() throws Exception {
        mockMvc.perform(get("/api/v1/seo/product/aventus-seo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.title").exists())
                .andExpect(jsonPath("$.data.canonicalUrl").value("https://scentiva.luxury/product/aventus-seo"))
                .andExpect(jsonPath("$.data.jsonLdSchema").exists());
    }

    @Test
    @DisplayName("GET /api/v1/seo/brand/{slug} should return brand SEO metadata and Schema.org")
    void shouldGetBrandSeo() throws Exception {
        mockMvc.perform(get("/api/v1/seo/brand/creed-seo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.canonicalUrl").value("https://scentiva.luxury/brands/creed-seo"))
                .andExpect(jsonPath("$.data.jsonLdSchema").exists());
    }

    @Test
    @DisplayName("GET /api/v1/seo/sitemap should return dynamic sitemap URLs")
    void shouldGetSitemap() throws Exception {
        mockMvc.perform(get("/api/v1/seo/sitemap"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data").isArray());
    }
}
