package com.scentiva.modules.promotion.service;

import com.scentiva.common.exception.ResourceNotFoundException;
import com.scentiva.modules.promotion.dto.CampaignCreateRequest;
import com.scentiva.modules.promotion.dto.CampaignResponse;
import com.scentiva.modules.promotion.model.Campaign;
import com.scentiva.modules.promotion.repository.CampaignRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CampaignServiceTest {

    @Autowired
    private CampaignService campaignService;

    @Autowired
    private CampaignRepository campaignRepository;

    @BeforeEach
    void setUp() {
        campaignRepository.deleteAll();

        // 1. Active running campaign
        campaignRepository.save(Campaign.builder()
                .title("Winter Oud Festival")
                .slug("winter-oud-festival")
                .description("Exclusive artisanal oud curation with complimentary miniature atomizers.")
                .bannerUrl("https://scentiva.luxury/banners/winter-oud.jpg")
                .discountPercentage(new BigDecimal("15.00"))
                .startsAt(LocalDateTime.now().minusDays(2))
                .endsAt(LocalDateTime.now().plusDays(10))
                .isActive(true)
                .build());

        // 2. Past expired campaign
        campaignRepository.save(Campaign.builder()
                .title("Autumn Amber Gala")
                .slug("autumn-amber-gala")
                .description("Past season archive")
                .discountPercentage(new BigDecimal("10.00"))
                .startsAt(LocalDateTime.now().minusDays(30))
                .endsAt(LocalDateTime.now().minusDays(5))
                .isActive(true)
                .build());
    }

    @Test
    @DisplayName("Should retrieve only active running campaigns")
    void shouldRetrieveActiveRunningCampaigns() {
        List<CampaignResponse> active = campaignService.getActiveCampaigns();

        assertThat(active).hasSize(1);
        assertThat(active.get(0).getSlug()).isEqualTo("winter-oud-festival");
        assertThat(active.get(0).isCurrentlyRunning()).isTrue();
    }

    @Test
    @DisplayName("Should retrieve campaign by slug")
    void shouldRetrieveCampaignBySlug() {
        CampaignResponse response = campaignService.getCampaignBySlug("winter-oud-festival");

        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo("Winter Oud Festival");
        assertThat(response.getDiscountPercentage()).isEqualByComparingTo(new BigDecimal("15.00"));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException for unknown campaign slug")
    void shouldThrowWhenSlugNotFound() {
        assertThatThrownBy(() -> campaignService.getCampaignBySlug("non-existent-campaign"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Should create new promotional campaign with auto-generated slug")
    void shouldCreateCampaignWithAutoSlug() {
        CampaignCreateRequest request = CampaignCreateRequest.builder()
                .title("Spring Bloom 2026")
                .description("Celebration of French grasse rose and bergamot harvest.")
                .bannerUrl("https://scentiva.luxury/banners/spring-bloom.jpg")
                .discountPercentage(new BigDecimal("20.00"))
                .startsAt(LocalDateTime.now())
                .endsAt(LocalDateTime.now().plusDays(15))
                .isActive(true)
                .build();

        CampaignResponse response = campaignService.createCampaign(request);

        assertThat(response.getId()).isNotNull();
        assertThat(response.getSlug()).isEqualTo("spring-bloom-2026");
        assertThat(response.getTitle()).isEqualTo("Spring Bloom 2026");
    }

    @Test
    @DisplayName("Should soft delete campaign")
    void shouldSoftDeleteCampaign() {
        CampaignResponse campaign = campaignService.getCampaignBySlug("winter-oud-festival");
        campaignService.deleteCampaign(campaign.getId());

        assertThatThrownBy(() -> campaignService.getCampaignBySlug("winter-oud-festival"))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
