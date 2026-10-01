package com.scentiva.modules.seo.service;

import com.scentiva.common.exception.ResourceNotFoundException;
import com.scentiva.modules.catalog.model.*;
import com.scentiva.modules.catalog.repository.BrandRepository;
import com.scentiva.modules.catalog.repository.CategoryRepository;
import com.scentiva.modules.catalog.repository.ProductRepository;
import com.scentiva.modules.cms.model.EditorialStory;
import com.scentiva.modules.cms.repository.EditorialStoryRepository;
import com.scentiva.modules.seo.dto.BrandSeoMetadataResponse;
import com.scentiva.modules.seo.dto.ProductSeoMetadataResponse;
import com.scentiva.modules.seo.dto.SitemapEntryDto;
import com.scentiva.modules.seo.service.impl.SeoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SeoServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private BrandRepository brandRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private EditorialStoryRepository storyRepository;

    @InjectMocks
    private SeoServiceImpl seoService;

    private Brand brand;
    private Product product;
    private EditorialStory story;

    @BeforeEach
    void setUp() {
        brand = Brand.builder()
                .name("Maison Francis Kurkdjian")
                .slug("maison-francis-kurkdjian")
                .originCountry("France")
                .description("French luxury fragrance house founded in Paris.")
                .tier(BrandTier.HERITAGE_MAISON)
                .build();

        product = Product.builder()
                .brand(brand)
                .name("Baccarat Rouge 540")
                .slug("baccarat-rouge-540")
                .description("Luminous and sophisticated woody amber floral fragrance.")
                .gender(GenderTarget.UNISEX)
                .isActive(true)
                .build();

        ProductVariant variant = ProductVariant.builder()
                .product(product)
                .sku("MFK-BR540-70")
                .volumeMl(70)
                .concentration(Concentration.EXTRAIT)
                .basePrice(new BigDecimal("35000.00"))
                .isActive(true)
                .build();

        product.setVariants(List.of(variant));

        story = EditorialStory.builder()
                .title("The Secrets of Grasse Perfumery")
                .slug("the-secrets-of-grasse-perfumery")
                .publishedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("getProductSeo should generate OpenGraph tags and Schema.org Product JSON-LD")
    void shouldGetProductSeo() {
        when(productRepository.findBySlugWithDetails("baccarat-rouge-540")).thenReturn(Optional.of(product));

        ProductSeoMetadataResponse response = seoService.getProductSeo("baccarat-rouge-540");

        assertThat(response).isNotNull();
        assertThat(response.getTitle()).contains("Baccarat Rouge 540").contains("Maison Francis Kurkdjian");
        assertThat(response.getCanonicalUrl()).isEqualTo("https://scentiva.luxury/product/baccarat-rouge-540");
        assertThat(response.getOpenGraph()).containsKey("og:title");
        assertThat(response.getJsonLdSchema()).contains("\"@type\": \"Product\"").contains("35000.00");
    }

    @Test
    @DisplayName("getProductSeo should throw ResourceNotFoundException for unknown product")
    void shouldThrowWhenProductSeoNotFound() {
        when(productRepository.findBySlugWithDetails("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> seoService.getProductSeo("unknown"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("getBrandSeo should generate brand metadata and Brand Schema")
    void shouldGetBrandSeo() {
        when(brandRepository.findBySlugAndIsDeletedFalse("maison-francis-kurkdjian")).thenReturn(Optional.of(brand));

        BrandSeoMetadataResponse response = seoService.getBrandSeo("maison-francis-kurkdjian");

        assertThat(response).isNotNull();
        assertThat(response.getTitle()).contains("Maison Francis Kurkdjian");
        assertThat(response.getCanonicalUrl()).isEqualTo("https://scentiva.luxury/brands/maison-francis-kurkdjian");
        assertThat(response.getJsonLdSchema()).contains("\"@type\": \"Brand\"");
    }

    @Test
    @DisplayName("generateSitemapEntries should return combined entries for static pages, products, brands, and stories")
    void shouldGenerateSitemapEntries() {
        when(productRepository.findAll()).thenReturn(List.of(product));
        when(brandRepository.findAll()).thenReturn(List.of(brand));
        when(storyRepository.findPublishedStories()).thenReturn(List.of(story));

        List<SitemapEntryDto> entries = seoService.generateSitemapEntries();

        assertThat(entries).isNotEmpty();
        assertThat(entries).anyMatch(e -> e.getUrl().contains("/product/baccarat-rouge-540"));
        assertThat(entries).anyMatch(e -> e.getUrl().contains("/brands/maison-francis-kurkdjian"));
        assertThat(entries).anyMatch(e -> e.getUrl().contains("/stories/the-secrets-of-grasse-perfumery"));
        assertThat(entries).anyMatch(e -> e.getUrl().equals("https://scentiva.luxury/"));
    }
}
