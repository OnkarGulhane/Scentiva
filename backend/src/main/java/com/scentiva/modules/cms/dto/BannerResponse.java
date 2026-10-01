package com.scentiva.modules.cms.dto;

import com.scentiva.modules.cms.model.BannerPlacement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BannerResponse {
    private Long id;
    private String title;
    private String subtitle;
    private String ctaText;
    private String ctaLink;
    private String imageUrl;
    private String mobileImageUrl;
    private BannerPlacement placement;
    private int displayOrder;
    private boolean isActive;
    private boolean isCurrentlyRunning;
    private LocalDateTime startsAt;
    private LocalDateTime endsAt;
    private LocalDateTime createdAt;
}
