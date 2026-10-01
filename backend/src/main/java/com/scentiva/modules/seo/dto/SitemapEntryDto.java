package com.scentiva.modules.seo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SitemapEntryDto {
    private String url;
    private String lastModified;
    private String changeFrequency; // "daily", "weekly", "monthly"
    private double priority;       // 0.0 - 1.0
}
