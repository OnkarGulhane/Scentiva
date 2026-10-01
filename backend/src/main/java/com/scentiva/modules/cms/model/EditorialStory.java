package com.scentiva.modules.cms.model;

import com.scentiva.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "editorial_stories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EditorialStory extends BaseEntity {

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "slug", nullable = false, unique = true)
    private String slug;

    @Column(name = "subtitle")
    private String subtitle;

    @Column(name = "excerpt", columnDefinition = "TEXT")
    private String excerpt;

    @Column(name = "content_html", nullable = false, columnDefinition = "TEXT")
    private String contentHtml;

    @Column(name = "cover_image_url", length = 500)
    private String coverImageUrl;

    @Column(name = "author_name", nullable = false, length = 150)
    @Builder.Default
    private String authorName = "Scentiva Editorial Guild";

    @Column(name = "reading_time_minutes", nullable = false)
    @Builder.Default
    private Integer readingTimeMinutes = 4;

    @Column(name = "is_featured", nullable = false)
    @Builder.Default
    private boolean isFeatured = false;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Column(name = "tags")
    private String tags; // comma separated
}
