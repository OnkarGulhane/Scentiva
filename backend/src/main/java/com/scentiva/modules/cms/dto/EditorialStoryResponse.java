package com.scentiva.modules.cms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EditorialStoryResponse {
    private Long id;
    private String title;
    private String slug;
    private String subtitle;
    private String excerpt;
    private String contentHtml;
    private String coverImageUrl;
    private String authorName;
    private int readingTimeMinutes;
    private boolean isFeatured;
    private LocalDateTime publishedAt;
    private String tags;
    private LocalDateTime createdAt;
}
