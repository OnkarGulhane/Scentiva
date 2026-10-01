package com.scentiva.modules.promotion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CampaignResponse {
    private Long id;
    private String title;
    private String slug;
    private String description;
    private String bannerUrl;
    private BigDecimal discountPercentage;
    private LocalDateTime startsAt;
    private LocalDateTime endsAt;
    private boolean isActive;
    private boolean isCurrentlyRunning;
    private LocalDateTime createdAt;
}
