package com.scentiva.modules.promotion.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scentiva.modules.promotion.dto.CampaignCreateRequest;
import com.scentiva.modules.promotion.model.Campaign;
import com.scentiva.modules.promotion.repository.CampaignRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class CampaignControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CampaignRepository campaignRepository;

    private Campaign campaign;

    @BeforeEach
    void setUp() {
        campaignRepository.deleteAll();

        campaign = campaignRepository.save(Campaign.builder()
                .title("Midsummer Rose Showcase")
                .slug("midsummer-rose")
                .description("Artisanal damascena rose extrait de parfum highlights.")
                .bannerUrl("https://scentiva.luxury/banners/rose.jpg")
                .discountPercentage(new BigDecimal("12.00"))
                .startsAt(LocalDateTime.now().minusDays(1))
                .endsAt(LocalDateTime.now().plusDays(10))
                .isActive(true)
                .build());
    }

    @Test
    @DisplayName("GET /api/v1/campaigns should list active campaigns publicly")
    void shouldGetActiveCampaignsPublic() throws Exception {
        mockMvc.perform(get("/api/v1/campaigns"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].slug", is("midsummer-rose")));
    }

    @Test
    @DisplayName("GET /api/v1/campaigns/{slug} should return campaign details")
    void shouldGetCampaignBySlug() throws Exception {
        mockMvc.perform(get("/api/v1/campaigns/midsummer-rose"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.title", is("Midsummer Rose Showcase")))
                .andExpect(jsonPath("$.data.discountPercentage", is(12.0)));
    }

    @Test
    @DisplayName("POST /api/v1/campaigns should create campaign for MARKETING_MANAGER")
    @WithMockUser(username = "marketing@scentiva.luxury", roles = "MARKETING_MANAGER")
    void shouldCreateCampaignForMarketingManager() throws Exception {
        CampaignCreateRequest request = CampaignCreateRequest.builder()
                .title("Autumn Bergamot Harvest")
                .slug("autumn-bergamot")
                .description("Calabrian citrus essence and vetiver blends.")
                .discountPercentage(new BigDecimal("18.00"))
                .startsAt(LocalDateTime.now())
                .endsAt(LocalDateTime.now().plusDays(20))
                .isActive(true)
                .build();

        mockMvc.perform(post("/api/v1/campaigns")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.slug", is("autumn-bergamot")));
    }

    @Test
    @DisplayName("DELETE /api/v1/campaigns/{id} should soft delete campaign for ADMIN")
    @WithMockUser(username = "admin@scentiva.luxury", roles = "ADMIN")
    void shouldDeleteCampaignForAdmin() throws Exception {
        mockMvc.perform(delete("/api/v1/campaigns/" + campaign.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));
    }
}
