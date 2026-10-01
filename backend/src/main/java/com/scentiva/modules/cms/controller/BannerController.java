package com.scentiva.modules.cms.controller;

import com.scentiva.common.response.ApiResponse;
import com.scentiva.modules.cms.dto.BannerCreateRequest;
import com.scentiva.modules.cms.dto.BannerResponse;
import com.scentiva.modules.cms.model.BannerPlacement;
import com.scentiva.modules.cms.service.BannerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/banners")
@RequiredArgsConstructor
@Tag(name = "Promotional Banners & Hero CMS", description = "Homepage hero sliders, promotional header banners and collection teasers")
public class BannerController {

    private final BannerService bannerService;

    @GetMapping
    @Operation(summary = "Get active banners", description = "Retrieves active scheduled promotional banners with optional placement filter.")
    public ResponseEntity<ApiResponse<List<BannerResponse>>> getActiveBanners(
            @RequestParam(required = false) BannerPlacement placement) {
        List<BannerResponse> banners = bannerService.getActiveBanners(placement);
        return ResponseEntity.ok(ApiResponse.ok(banners, "Banners retrieved successfully"));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'MARKETING_MANAGER')")
    @Operation(summary = "Create banner (Admin)", description = "Schedules a new promotional visual banner.")
    public ResponseEntity<ApiResponse<BannerResponse>> createBanner(
            @Valid @RequestBody BannerCreateRequest request) {
        BannerResponse banner = bannerService.createBanner(request);
        return ResponseEntity.ok(ApiResponse.ok(banner, "Banner created successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'MARKETING_MANAGER')")
    @Operation(summary = "Delete banner (Admin)", description = "Soft deletes a promotional banner.")
    public ResponseEntity<ApiResponse<Void>> deleteBanner(@PathVariable Long id) {
        bannerService.deleteBanner(id);
        return ResponseEntity.ok(ApiResponse.ok(null, "Banner deleted successfully"));
    }
}
