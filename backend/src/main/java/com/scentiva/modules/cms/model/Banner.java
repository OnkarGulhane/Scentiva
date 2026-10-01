package com.scentiva.modules.cms.model;

import com.scentiva.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "banners")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Banner extends BaseEntity {

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "subtitle")
    private String subtitle;

    @Column(name = "cta_text", length = 100)
    private String ctaText;

    @Column(name = "cta_link")
    private String ctaLink;

    @Column(name = "image_url", nullable = false, length = 500)
    private String imageUrl;

    @Column(name = "mobile_image_url", length = 500)
    private String mobileImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "placement", nullable = false, length = 50)
    @Builder.Default
    private BannerPlacement placement = BannerPlacement.HERO_CAROUSEL;

    @Column(name = "display_order", nullable = false)
    @Builder.Default
    private Integer displayOrder = 0;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean isActive = true;

    @Column(name = "starts_at")
    private LocalDateTime startsAt;

    @Column(name = "ends_at")
    private LocalDateTime endsAt;

    public boolean isCurrentlyRunning() {
        if (!isActive) return false;
        LocalDateTime now = LocalDateTime.now();
        if (startsAt != null && now.isBefore(startsAt)) return false;
        if (endsAt != null && now.isAfter(endsAt)) return false;
        return true;
    }
}
