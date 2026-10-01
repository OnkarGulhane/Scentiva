package com.scentiva.modules.review.dto;

import com.scentiva.modules.review.model.ReviewStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewModerationRequest {

    @NotNull(message = "Moderation status is required")
    private ReviewStatus status;
}
