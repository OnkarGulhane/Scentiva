package com.scentiva.modules.promotion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class CampaignCreateRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String slug;

    private String description;

    private String bannerUrl;

    private BigDecimal discountPercentage;

    @NotNull(message = "Start date is required")
    private LocalDateTime startsAt;

    @NotNull(message = "End date is required")
    private LocalDateTime endsAt;

    @Builder.Default
    private boolean isActive = true;
}
