package com.scentiva.modules.seo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSeoMetadataResponse {
    private String title;
    private String description;
    private String canonicalUrl;
    private Map<String, String> openGraph;
    private String jsonLdSchema;
}
