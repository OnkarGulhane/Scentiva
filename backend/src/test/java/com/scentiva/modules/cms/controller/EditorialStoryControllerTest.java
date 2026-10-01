package com.scentiva.modules.cms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scentiva.modules.cms.dto.EditorialStoryCreateRequest;
import com.scentiva.modules.cms.model.EditorialStory;
import com.scentiva.modules.cms.repository.EditorialStoryRepository;
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
class EditorialStoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EditorialStoryRepository storyRepository;

    private EditorialStory story;

    @BeforeEach
    void setUp() {
        story = storyRepository.findBySlugAndIsDeletedFalse("the-craft-of-grasse-jasmine")
                .orElseGet(() -> storyRepository.save(EditorialStory.builder()
                        .title("The Craft of Grasse Jasmine")
                        .slug("the-craft-of-grasse-jasmine")
                        .subtitle("Night harvesting white blossoms")
                        .excerpt("Harvesting nocturnal jasmine blossoms before dawn...")
                        .contentHtml("<p>In Grasse, jasmine is picked by hand under moonlight...</p>")
                        .coverImageUrl("https://images.scentiva.com/stories/jasmine.webp")
                        .authorName("Dominique Ropion")
                        .readingTimeMinutes(5)
                        .isFeatured(true)
                        .tags("Jasmine,Grasse,Artisanship")
                        .publishedAt(LocalDateTime.now())
                        .build()));
    }

    @Test
    @DisplayName("GET /api/v1/stories should return paginated published stories")
    void shouldGetPublishedStories() throws Exception {
        mockMvc.perform(get("/api/v1/stories")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.totalElements").exists());
    }

    @Test
    @DisplayName("GET /api/v1/stories/featured should return featured stories")
    void shouldGetFeaturedStories() throws Exception {
        mockMvc.perform(get("/api/v1/stories/featured"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("GET /api/v1/stories/{slug} should return story by slug")
    void shouldGetStoryBySlug() throws Exception {
        mockMvc.perform(get("/api/v1/stories/" + story.getSlug()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.slug", is("the-craft-of-grasse-jasmine")))
                .andExpect(jsonPath("$.data.authorName", is("Dominique Ropion")));
    }

    @Test
    @DisplayName("POST /api/v1/stories as ADMIN should create new story")
    @WithMockUser(username = "admin@scentiva.com", roles = {"ADMIN"})
    void shouldCreateStoryAsAdmin() throws Exception {
        EditorialStoryCreateRequest request = EditorialStoryCreateRequest.builder()
                .title("Ambergris & Myth: Oceanic Treasures")
                .slug("ambergris-and-myth")
                .subtitle("The floating gold of the perfumer")
                .excerpt("A rare oceanic encounter with floating ambergris...")
                .contentHtml("<p>Ambergris has fascinated perfumers for centuries...</p>")
                .authorName("Scentiva Guild")
                .readingTimeMinutes(4)
                .isFeatured(false)
                .build();

        mockMvc.perform(post("/api/v1/stories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.title", is("Ambergris & Myth: Oceanic Treasures")))
                .andExpect(jsonPath("$.data.slug", is("ambergris-and-myth")));
    }

    @Test
    @DisplayName("DELETE /api/v1/stories/{id} as ADMIN should delete story")
    @WithMockUser(username = "admin@scentiva.com", roles = {"ADMIN"})
    void shouldDeleteStoryAsAdmin() throws Exception {
        mockMvc.perform(delete("/api/v1/stories/" + story.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));
    }
}
