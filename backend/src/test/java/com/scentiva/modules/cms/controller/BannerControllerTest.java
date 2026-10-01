package com.scentiva.modules.cms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scentiva.modules.cms.dto.BannerCreateRequest;
import com.scentiva.modules.cms.model.Banner;
import com.scentiva.modules.cms.model.BannerPlacement;
import com.scentiva.modules.cms.repository.BannerRepository;
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

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class BannerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BannerRepository bannerRepository;

    private Banner banner;

    @BeforeEach
    void setUp() {
        banner = bannerRepository.save(Banner.builder()
                .title("Autumn Oud Nocturne")
                .subtitle("Rare harvest agarwood oils from Assam")
                .ctaText("Explore Oud")
                .ctaLink("/collections/oud-nocturne")
                .imageUrl("https://images.scentiva.com/banners/oud-hero.webp")
                .mobileImageUrl("https://images.scentiva.com/banners/oud-hero-mob.webp")
                .placement(BannerPlacement.HERO_CAROUSEL)
                .displayOrder(1)
                .isActive(true)
                .startsAt(LocalDateTime.now().minusDays(1))
                .endsAt(LocalDateTime.now().plusDays(30))
                .build());
    }

    @Test
    @DisplayName("GET /api/v1/banners should return active promotional banners")
    void shouldGetActiveBanners() throws Exception {
        mockMvc.perform(get("/api/v1/banners"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("GET /api/v1/banners with placement filter should return banners for that slot")
    void shouldGetBannersByPlacement() throws Exception {
        mockMvc.perform(get("/api/v1/banners")
                        .param("placement", "HERO_CAROUSEL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("POST /api/v1/banners as ADMIN should schedule new banner")
    @WithMockUser(username = "admin@scentiva.com", roles = {"ADMIN"})
    void shouldCreateBannerAsAdmin() throws Exception {
        BannerCreateRequest request = BannerCreateRequest.builder()
                .title("Privée Discovery Set")
                .subtitle("Complimentary luxury discovery coffret")
                .ctaText("Discover Now")
                .ctaLink("/collections/discovery-sets")
                .imageUrl("https://images.scentiva.com/banners/discovery.webp")
                .placement(BannerPlacement.CATEGORY_HEADER)
                .displayOrder(2)
                .isActive(true)
                .build();

        mockMvc.perform(post("/api/v1/banners")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.title", is("Privée Discovery Set")))
                .andExpect(jsonPath("$.data.placement", is("CATEGORY_HEADER")));
    }

    @Test
    @DisplayName("DELETE /api/v1/banners/{id} as ADMIN should delete banner")
    @WithMockUser(username = "admin@scentiva.com", roles = {"ADMIN"})
    void shouldDeleteBannerAsAdmin() throws Exception {
        mockMvc.perform(delete("/api/v1/banners/" + banner.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));
    }
}
