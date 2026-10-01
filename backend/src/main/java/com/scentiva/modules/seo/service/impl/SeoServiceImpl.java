package com.scentiva.modules.seo.service.impl;

import com.scentiva.common.exception.ResourceNotFoundException;
import com.scentiva.modules.catalog.model.Brand;
import com.scentiva.modules.catalog.model.Product;
import com.scentiva.modules.catalog.model.ProductImage;
import com.scentiva.modules.catalog.model.ProductVariant;
import com.scentiva.modules.catalog.repository.BrandRepository;
import com.scentiva.modules.catalog.repository.CategoryRepository;
import com.scentiva.modules.catalog.repository.ProductRepository;
import com.scentiva.modules.cms.model.EditorialStory;
import com.scentiva.modules.cms.repository.EditorialStoryRepository;
import com.scentiva.modules.seo.dto.BrandSeoMetadataResponse;
import com.scentiva.modules.seo.dto.ProductSeoMetadataResponse;
import com.scentiva.modules.seo.dto.SitemapEntryDto;
import com.scentiva.modules.seo.service.SeoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class SeoServiceImpl implements SeoService {

    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;
    private final EditorialStoryRepository storyRepository;

    private static final String BASE_URL = "https://scentiva.luxury";

    @Override
    @Transactional(readOnly = true)
    public ProductSeoMetadataResponse getProductSeo(String slug) {
        Product product = productRepository.findBySlugWithDetails(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "slug", slug));

        String title = product.getName() + " by " + product.getBrand().getName() + " | SCENTIVA Haute Parfumerie";
        String description = (product.getDescription() != null && !product.getDescription().isEmpty())
                ? product.getDescription()
                : "Discover " + product.getName() + " from " + product.getBrand().getName() + ". Authentic luxury flacon with complimentary white-glove concierge dispatch.";

        String canonicalUrl = BASE_URL + "/product/" + product.getSlug();

        String imageUrl = (product.getImages() != null && !product.getImages().isEmpty())
                ? product.getImages().stream().filter(ProductImage::isPrimary).map(ProductImage::getImageUrl).findFirst().orElse(product.getImages().get(0).getImageUrl())
                : BASE_URL + "/images/og-default.jpg";

        Map<String, String> openGraph = new HashMap<>();
        openGraph.put("og:title", title);
        openGraph.put("og:description", description);
        openGraph.put("og:url", canonicalUrl);
        openGraph.put("og:image", imageUrl);
        openGraph.put("og:type", "product");

        BigDecimal minPrice = product.getVariants().stream()
                .filter(ProductVariant::isActive)
                .map(ProductVariant::getEffectivePrice)
                .min(Comparator.naturalOrder())
                .orElse(new BigDecimal("15000.00"));

        String jsonLd = String.format("""
                {
                  "@context": "https://schema.org/",
                  "@type": "Product",
                  "name": "%s",
                  "image": "%s",
                  "description": "%s",
                  "brand": {
                    "@type": "Brand",
                    "name": "%s"
                  },
                  "offers": {
                    "@type": "Offer",
                    "priceCurrency": "INR",
                    "price": "%s",
                    "availability": "https://schema.org/InStock",
                    "url": "%s"
                  }
                }
                """, product.getName(), imageUrl, description.replace("\"", "\\\""), product.getBrand().getName(), minPrice, canonicalUrl);

        return ProductSeoMetadataResponse.builder()
                .title(title)
                .description(description)
                .canonicalUrl(canonicalUrl)
                .openGraph(openGraph)
                .jsonLdSchema(jsonLd.trim())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public BrandSeoMetadataResponse getBrandSeo(String slug) {
        Brand brand = brandRepository.findBySlugAndIsDeletedFalse(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Brand", "slug", slug));

        String title = brand.getName() + " Perfumes & Fragrance Archive | SCENTIVA";
        String description = (brand.getDescription() != null && !brand.getDescription().isEmpty())
                ? brand.getDescription()
                : "Explore the complete haute parfumerie universe of " + brand.getName() + " (" + brand.getOriginCountry() + "). Certified authentic luxury bottles at Scentiva.";

        String canonicalUrl = BASE_URL + "/brands/" + brand.getSlug();

        Map<String, String> openGraph = new HashMap<>();
        openGraph.put("og:title", title);
        openGraph.put("og:description", description);
        openGraph.put("og:url", canonicalUrl);
        openGraph.put("og:type", "website");

        String jsonLd = String.format("""
                {
                  "@context": "https://schema.org/",
                  "@type": "Brand",
                  "name": "%s",
                  "url": "%s",
                  "description": "%s"
                }
                """, brand.getName(), canonicalUrl, description.replace("\"", "\\\""));

        return BrandSeoMetadataResponse.builder()
                .title(title)
                .description(description)
                .canonicalUrl(canonicalUrl)
                .openGraph(openGraph)
                .jsonLdSchema(jsonLd.trim())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SitemapEntryDto> generateSitemapEntries() {
        List<SitemapEntryDto> entries = new ArrayList<>();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // Static routes
        entries.add(new SitemapEntryDto(BASE_URL + "/", "2026-10-01", "daily", 1.0));
        entries.add(new SitemapEntryDto(BASE_URL + "/shop", "2026-10-01", "daily", 0.9));
        entries.add(new SitemapEntryDto(BASE_URL + "/brands", "2026-10-01", "weekly", 0.8));
        entries.add(new SitemapEntryDto(BASE_URL + "/stories", "2026-10-01", "weekly", 0.8));
        entries.add(new SitemapEntryDto(BASE_URL + "/find-your-scent", "2026-10-01", "monthly", 0.7));

        // Products
        for (Product product : productRepository.findAll()) {
            if (product.isActive() && !product.isDeleted()) {
                String lastmod = product.getUpdatedAt() != null ? product.getUpdatedAt().format(dtf) : "2026-10-01";
                entries.add(new SitemapEntryDto(BASE_URL + "/product/" + product.getSlug(), lastmod, "daily", 0.9));
            }
        }

        // Brands
        for (Brand brand : brandRepository.findAll()) {
            if (!brand.isDeleted()) {
                entries.add(new SitemapEntryDto(BASE_URL + "/brands/" + brand.getSlug(), "2026-10-01", "weekly", 0.8));
            }
        }

        // Stories
        for (EditorialStory story : storyRepository.findPublishedStories()) {
            String lastmod = story.getPublishedAt() != null ? story.getPublishedAt().format(dtf) : "2026-10-01";
            entries.add(new SitemapEntryDto(BASE_URL + "/stories/" + story.getSlug(), lastmod, "weekly", 0.7));
        }

        return entries;
    }
}
