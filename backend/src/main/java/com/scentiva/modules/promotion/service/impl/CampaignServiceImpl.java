package com.scentiva.modules.promotion.service.impl;

import com.scentiva.common.exception.ResourceNotFoundException;
import com.scentiva.modules.promotion.dto.CampaignCreateRequest;
import com.scentiva.modules.promotion.dto.CampaignResponse;
import com.scentiva.modules.promotion.model.Campaign;
import com.scentiva.modules.promotion.repository.CampaignRepository;
import com.scentiva.modules.promotion.service.CampaignService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class CampaignServiceImpl implements CampaignService {

    private final CampaignRepository campaignRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CampaignResponse> getActiveCampaigns() {
        return campaignRepository.findActiveRunningCampaigns(LocalDateTime.now()).stream()
                .map(this::mapToCampaignResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CampaignResponse> getAllCampaigns() {
        return campaignRepository.findAll().stream()
                .filter(c -> !c.isDeleted())
                .map(this::mapToCampaignResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CampaignResponse getCampaignBySlug(String slug) {
        Campaign campaign = campaignRepository.findBySlugAndIsDeletedFalse(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign", "slug", slug));
        return mapToCampaignResponse(campaign);
    }

    @Override
    @Transactional
    public CampaignResponse createCampaign(CampaignCreateRequest request) {
        String slug = (request.getSlug() != null && !request.getSlug().trim().isEmpty())
                ? request.getSlug().trim().toLowerCase(Locale.ROOT)
                : request.getTitle().trim().toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", "-");

        if (campaignRepository.findBySlugAndIsDeletedFalse(slug).isPresent()) {
            throw new IllegalArgumentException("Campaign with slug '" + slug + "' already exists");
        }

        Campaign campaign = Campaign.builder()
                .title(request.getTitle())
                .slug(slug)
                .description(request.getDescription())
                .bannerUrl(request.getBannerUrl())
                .discountPercentage(request.getDiscountPercentage())
                .startsAt(request.getStartsAt())
                .endsAt(request.getEndsAt())
                .isActive(request.isActive())
                .build();

        campaign = campaignRepository.save(campaign);
        log.info("Created promotional campaign id={}, title={}", campaign.getId(), campaign.getTitle());

        return mapToCampaignResponse(campaign);
    }

    @Override
    @Transactional
    public void deleteCampaign(Long id) {
        Campaign campaign = campaignRepository.findById(id)
                .filter(c -> !c.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Campaign", "id", id));
        campaign.setDeleted(true);
        campaignRepository.save(campaign);
        log.info("Soft deleted campaign id={}, title={}", campaign.getId(), campaign.getTitle());
    }

    private CampaignResponse mapToCampaignResponse(Campaign campaign) {
        return CampaignResponse.builder()
                .id(campaign.getId())
                .title(campaign.getTitle())
                .slug(campaign.getSlug())
                .description(campaign.getDescription())
                .bannerUrl(campaign.getBannerUrl())
                .discountPercentage(campaign.getDiscountPercentage())
                .startsAt(campaign.getStartsAt())
                .endsAt(campaign.getEndsAt())
                .isActive(campaign.isActive())
                .isCurrentlyRunning(campaign.isCurrentlyRunning())
                .createdAt(campaign.getCreatedAt())
                .build();
    }
}
