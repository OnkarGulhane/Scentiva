package com.scentiva.modules.promotion.controller;

import com.scentiva.common.response.ApiResponse;
import com.scentiva.modules.promotion.dto.CampaignCreateRequest;
import com.scentiva.modules.promotion.dto.CampaignResponse;
import com.scentiva.modules.promotion.service.CampaignService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/campaigns")
@RequiredArgsConstructor
@Tag(name = "Marketing Campaigns", description = "Seasonal perfume campaigns, holiday flash sales and editorial promotions")
public class CampaignController {

    private final CampaignService campaignService;

    @GetMapping
    @Operation(summary = "Get active campaigns", description = "Retrieves currently running promotional marketing campaigns.")
    public ResponseEntity<ApiResponse<List<CampaignResponse>>> getActiveCampaigns() {
        List<CampaignResponse> campaigns = campaignService.getActiveCampaigns();
        return ResponseEntity.ok(ApiResponse.ok(campaigns, "Active campaigns retrieved"));
    }

    @GetMapping("/{slug}")
    @Operation(summary = "Get campaign by slug", description = "Retrieves campaign details and promotional imagery by slug.")
    public ResponseEntity<ApiResponse<CampaignResponse>> getCampaignBySlug(@PathVariable String slug) {
        CampaignResponse campaign = campaignService.getCampaignBySlug(slug);
        return ResponseEntity.ok(ApiResponse.ok(campaign, "Campaign retrieved successfully"));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'MARKETING_MANAGER')")
    @Operation(summary = "Create campaign (Admin)", description = "Creates a new seasonal promotional campaign.")
    public ResponseEntity<ApiResponse<CampaignResponse>> createCampaign(@Valid @RequestBody CampaignCreateRequest request) {
        CampaignResponse campaign = campaignService.createCampaign(request);
        return ResponseEntity.ok(ApiResponse.ok(campaign, "Campaign created successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'MARKETING_MANAGER')")
    @Operation(summary = "Delete campaign (Admin)", description = "Soft deletes a promotional campaign.")
    public ResponseEntity<ApiResponse<Void>> deleteCampaign(@PathVariable Long id) {
        campaignService.deleteCampaign(id);
        return ResponseEntity.ok(ApiResponse.ok(null, "Campaign deleted successfully"));
    }
}
