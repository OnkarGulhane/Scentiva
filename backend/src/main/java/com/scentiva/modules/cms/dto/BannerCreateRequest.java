package com.scentiva.modules.cms.dto;

import com.scentiva.modules.cms.model.BannerPlacement;
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
public class BannerCreateRequest {
    @NotBlank(message = "Title is required")
    private String title;

    private String subtitle;
    private String ctaText;
    private String ctaLink;

    @NotBlank(message = "Image URL is required")
    private String imageUrl;

    private String mobileImageUrl;

    @Builder.Default
    private BannerPlacement placement = BannerPlacement.HERO_CAROUSEL;

    @Builder.Default
    private int displayOrder = 0;

    @Builder.Default
    private boolean isActive = true;

    private LocalDateTime startsAt;
    private LocalDateTime endsAt;
}
