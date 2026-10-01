package com.scentiva.modules.cms.service.impl;

import com.scentiva.common.exception.ResourceNotFoundException;
import com.scentiva.modules.cms.dto.BannerCreateRequest;
import com.scentiva.modules.cms.dto.BannerResponse;
import com.scentiva.modules.cms.model.Banner;
import com.scentiva.modules.cms.model.BannerPlacement;
import com.scentiva.modules.cms.repository.BannerRepository;
import com.scentiva.modules.cms.service.BannerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BannerServiceImpl implements BannerService {

    private final BannerRepository bannerRepository;

    @Override
    @Transactional(readOnly = true)
    public List<BannerResponse> getActiveBanners(BannerPlacement placement) {
        List<Banner> banners = (placement != null)
                ? bannerRepository.findActiveBannersByPlacement(placement)
                : bannerRepository.findCurrentlyActiveBanners();

        return banners.stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BannerResponse> getAllBanners() {
        return bannerRepository.findByIsDeletedFalseOrderByDisplayOrderAsc().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public BannerResponse createBanner(BannerCreateRequest request) {
        Banner banner = Banner.builder()
                .title(request.getTitle())
                .subtitle(request.getSubtitle())
                .ctaText(request.getCtaText())
                .ctaLink(request.getCtaLink())
                .imageUrl(request.getImageUrl())
                .mobileImageUrl(request.getMobileImageUrl())
                .placement(request.getPlacement() != null ? request.getPlacement() : BannerPlacement.HERO_CAROUSEL)
                .displayOrder(request.getDisplayOrder())
                .isActive(request.isActive())
                .startsAt(request.getStartsAt())
                .endsAt(request.getEndsAt())
                .build();

        banner = bannerRepository.save(banner);
        log.info("Created promotional banner id={}, title='{}', placement={}", banner.getId(), banner.getTitle(), banner.getPlacement());

        return mapToResponse(banner);
    }

    @Override
    @Transactional
    public void deleteBanner(Long id) {
        Banner banner = bannerRepository.findById(id)
                .filter(b -> !b.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Banner", "id", id));

        banner.setDeleted(true);
        bannerRepository.save(banner);
        log.info("Soft deleted promotional banner id={}, title='{}'", banner.getId(), banner.getTitle());
    }

    private BannerResponse mapToResponse(Banner banner) {
        return BannerResponse.builder()
                .id(banner.getId())
                .title(banner.getTitle())
                .subtitle(banner.getSubtitle())
                .ctaText(banner.getCtaText())
                .ctaLink(banner.getCtaLink())
                .imageUrl(banner.getImageUrl())
                .mobileImageUrl(banner.getMobileImageUrl())
                .placement(banner.getPlacement())
                .displayOrder(banner.getDisplayOrder())
                .isActive(banner.isActive())
                .isCurrentlyRunning(banner.isCurrentlyRunning())
                .startsAt(banner.getStartsAt())
                .endsAt(banner.getEndsAt())
                .createdAt(banner.getCreatedAt())
                .build();
    }
}
