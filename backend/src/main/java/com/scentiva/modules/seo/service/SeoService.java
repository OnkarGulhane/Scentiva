package com.scentiva.modules.seo.service;

import com.scentiva.modules.seo.dto.BrandSeoMetadataResponse;
import com.scentiva.modules.seo.dto.ProductSeoMetadataResponse;
import com.scentiva.modules.seo.dto.SitemapEntryDto;

import java.util.List;

public interface SeoService {

    ProductSeoMetadataResponse getProductSeo(String slug);

    BrandSeoMetadataResponse getBrandSeo(String slug);

    List<SitemapEntryDto> generateSitemapEntries();
}
