package com.scentiva.modules.cms.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EditorialStoryCreateRequest {
    @NotBlank(message = "Title is required")
    private String title;

    private String slug;
    private String subtitle;
    private String excerpt;

    @NotBlank(message = "HTML content is required")
    private String contentHtml;

    private String coverImageUrl;
    private String authorName;

    @Builder.Default
    private int readingTimeMinutes = 4;

    private boolean isFeatured;
    private LocalDateTime publishedAt;
    private String tags;
}
