package com.scentiva.modules.seo.controller;

import com.scentiva.common.response.ApiResponse;
import com.scentiva.modules.seo.dto.BrandSeoMetadataResponse;
import com.scentiva.modules.seo.dto.ProductSeoMetadataResponse;
import com.scentiva.modules.seo.dto.SitemapEntryDto;
import com.scentiva.modules.seo.service.SeoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/seo")
@RequiredArgsConstructor
@Tag(name = "SEO & Schema.org Metadata", description = "Dynamic JSON-LD microdata, OpenGraph tags, canonical links and XML sitemap feeder")
public class SeoController {

    private final SeoService seoService;

    @GetMapping("/product/{slug}")
    @Operation(summary = "Get product SEO & Schema.org JSON-LD", description = "Provides meta title, description, and Product schema for rich search results.")
    public ResponseEntity<ApiResponse<ProductSeoMetadataResponse>> getProductSeo(@PathVariable String slug) {
        ProductSeoMetadataResponse response = seoService.getProductSeo(slug);
        return ResponseEntity.ok(ApiResponse.ok(response, "Product SEO metadata"));
    }

    @GetMapping("/brand/{slug}")
    @Operation(summary = "Get brand SEO metadata", description = "Provides brand archive metadata and Brand schema.")
    public ResponseEntity<ApiResponse<BrandSeoMetadataResponse>> getBrandSeo(@PathVariable String slug) {
        BrandSeoMetadataResponse response = seoService.getBrandSeo(slug);
        return ResponseEntity.ok(ApiResponse.ok(response, "Brand SEO metadata"));
    }

    @GetMapping("/sitemap")
    @Operation(summary = "Get dynamic sitemap entries", description = "Returns dynamic URLs, change frequencies and priorities for sitemap generation.")
    public ResponseEntity<ApiResponse<List<SitemapEntryDto>>> getSitemapEntries() {
        List<SitemapEntryDto> entries = seoService.generateSitemapEntries();
        return ResponseEntity.ok(ApiResponse.ok(entries, "Sitemap data generated"));
    }
}
