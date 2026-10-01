package com.scentiva.modules.cms.service;

import com.scentiva.common.exception.ResourceNotFoundException;
import com.scentiva.modules.cms.dto.BannerCreateRequest;
import com.scentiva.modules.cms.dto.BannerResponse;
import com.scentiva.modules.cms.model.Banner;
import com.scentiva.modules.cms.model.BannerPlacement;
import com.scentiva.modules.cms.repository.BannerRepository;
import com.scentiva.modules.cms.service.impl.BannerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BannerServiceTest {

    @Mock
    private BannerRepository bannerRepository;

    @InjectMocks
    private BannerServiceImpl bannerService;

    private Banner sampleBanner;

    @BeforeEach
    void setUp() {
        sampleBanner = Banner.builder()
                .title("Winter Extrait de Parfum Collection")
                .subtitle("Discover rare extrait creations from Grasse & Paris")
                .ctaText("Explore Collection")
                .ctaLink("/collections/winter-extraits")
                .imageUrl("https://images.scentiva.com/banners/winter-hero.webp")
                .mobileImageUrl("https://images.scentiva.com/banners/winter-hero-mob.webp")
                .placement(BannerPlacement.HERO_CAROUSEL)
                .displayOrder(1)
                .isActive(true)
                .startsAt(LocalDateTime.now().minusDays(1))
                .endsAt(LocalDateTime.now().plusDays(30))
                .build();
        sampleBanner.setId(1L);
    }

    @Test
    @DisplayName("getActiveBanners with placement should filter by placement")
    void shouldGetActiveBannersByPlacement() {
        when(bannerRepository.findActiveBannersByPlacement(BannerPlacement.HERO_CAROUSEL))
                .thenReturn(List.of(sampleBanner));

        List<BannerResponse> result = bannerService.getActiveBanners(BannerPlacement.HERO_CAROUSEL);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getTitle()).isEqualTo("Winter Extrait de Parfum Collection");
        assertThat(result.getFirst().getPlacement()).isEqualTo(BannerPlacement.HERO_CAROUSEL);
    }

    @Test
    @DisplayName("getActiveBanners with null placement should return all currently active banners")
    void shouldGetAllActiveBannersWhenPlacementNull() {
        when(bannerRepository.findCurrentlyActiveBanners()).thenReturn(List.of(sampleBanner));

        List<BannerResponse> result = bannerService.getActiveBanners(null);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getTitle()).isEqualTo("Winter Extrait de Parfum Collection");
    }

    @Test
    @DisplayName("getAllBanners should return all non-deleted banners")
    void shouldGetAllBanners() {
        when(bannerRepository.findByIsDeletedFalseOrderByDisplayOrderAsc()).thenReturn(List.of(sampleBanner));

        List<BannerResponse> result = bannerService.getAllBanners();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("createBanner should persist and return new banner")
    void shouldCreateBanner() {
        BannerCreateRequest request = BannerCreateRequest.builder()
                .title("Spring Floral Pre-Order")
                .subtitle("Exclusive early access")
                .ctaText("Pre-order Now")
                .ctaLink("/collections/spring-florals")
                .imageUrl("https://images.scentiva.com/banners/spring.webp")
                .placement(BannerPlacement.CATEGORY_HEADER)
                .displayOrder(2)
                .isActive(true)
                .build();

        Banner created = Banner.builder()
                .title(request.getTitle())
                .subtitle(request.getSubtitle())
                .ctaText(request.getCtaText())
                .ctaLink(request.getCtaLink())
                .imageUrl(request.getImageUrl())
                .placement(request.getPlacement())
                .displayOrder(request.getDisplayOrder())
                .isActive(true)
                .build();
        created.setId(2L);

        when(bannerRepository.save(any(Banner.class))).thenReturn(created);

        BannerResponse response = bannerService.createBanner(request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(2L);
        assertThat(response.getTitle()).isEqualTo("Spring Floral Pre-Order");
        verify(bannerRepository, times(1)).save(any(Banner.class));
    }

    @Test
    @DisplayName("deleteBanner should soft-delete banner")
    void shouldDeleteBanner() {
        when(bannerRepository.findById(1L)).thenReturn(Optional.of(sampleBanner));
        when(bannerRepository.save(any(Banner.class))).thenReturn(sampleBanner);

        bannerService.deleteBanner(1L);

        verify(bannerRepository, times(1)).save(sampleBanner);
        assertThat(sampleBanner.isDeleted()).isTrue();
    }

    @Test
    @DisplayName("deleteBanner should throw when banner not found")
    void shouldThrowWhenDeleteNotFound() {
        when(bannerRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bannerService.deleteBanner(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
