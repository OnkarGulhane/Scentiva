package com.scentiva.modules.cms.service;

import com.scentiva.common.exception.ResourceNotFoundException;
import com.scentiva.common.response.ApiPaginatedResponse;
import com.scentiva.modules.cms.dto.EditorialStoryCreateRequest;
import com.scentiva.modules.cms.dto.EditorialStoryResponse;
import com.scentiva.modules.cms.model.EditorialStory;
import com.scentiva.modules.cms.repository.EditorialStoryRepository;
import com.scentiva.modules.cms.service.impl.CmsStoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CmsStoryServiceTest {

    @Mock
    private EditorialStoryRepository storyRepository;

    @InjectMocks
    private CmsStoryServiceImpl storyService;

    private EditorialStory sampleStory;

    @BeforeEach
    void setUp() {
        sampleStory = EditorialStory.builder()
                .title("The Alchemy of Grasse Rose")
                .slug("the-alchemy-of-grasse-rose")
                .authorName("Jean-Claude Ellena")
                .subtitle("A journey into dawn-harvested centifolia petals")
                .excerpt("Every May at dawn, Grasse awakes to the harvest...")
                .contentHtml("<p>Every May at dawn, Grasse awakes to the harvest of Centifolia...</p>")
                .coverImageUrl("https://images.scentiva.com/stories/grasse.webp")
                .readingTimeMinutes(6)
                .isFeatured(true)
                .tags("Grasse,Centifolia,Rose")
                .publishedAt(LocalDateTime.now().minusDays(2))
                .build();
    }

    @Test
    @DisplayName("getPublishedStories should return paginated list of published stories")
    void shouldGetPublishedStories() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<EditorialStory> page = new PageImpl<>(List.of(sampleStory), pageable, 1);
        when(storyRepository.findByIsDeletedFalseOrderByPublishedAtDesc(pageable)).thenReturn(page);

        ApiPaginatedResponse<EditorialStoryResponse> response = storyService.getPublishedStories(pageable);

        assertThat(response).isNotNull();
        assertThat(response.getItems()).hasSize(1);
        assertThat(response.getItems().getFirst().getTitle()).isEqualTo("The Alchemy of Grasse Rose");
    }

    @Test
    @DisplayName("getFeaturedStories should return hero spotlight stories")
    void shouldGetFeaturedStories() {
        when(storyRepository.findByIsFeaturedTrueAndIsDeletedFalseOrderByPublishedAtDesc())
                .thenReturn(List.of(sampleStory));

        List<EditorialStoryResponse> featured = storyService.getFeaturedStories();

        assertThat(featured).hasSize(1);
        assertThat(featured.getFirst().getSlug()).isEqualTo("the-alchemy-of-grasse-rose");
        assertThat(featured.getFirst().isFeatured()).isTrue();
    }

    @Test
    @DisplayName("getStoryBySlug should return published story by slug")
    void shouldGetStoryBySlug() {
        when(storyRepository.findBySlugAndIsDeletedFalse("the-alchemy-of-grasse-rose"))
                .thenReturn(Optional.of(sampleStory));

        EditorialStoryResponse response = storyService.getStoryBySlug("the-alchemy-of-grasse-rose");

        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo("The Alchemy of Grasse Rose");
        assertThat(response.getAuthorName()).isEqualTo("Jean-Claude Ellena");
    }

    @Test
    @DisplayName("getStoryBySlug should throw ResourceNotFoundException when slug not found")
    void shouldThrowWhenSlugNotFound() {
        when(storyRepository.findBySlugAndIsDeletedFalse("non-existent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> storyService.getStoryBySlug("non-existent"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("EditorialStory");
    }

    @Test
    @DisplayName("createStory should save new editorial story with auto-generated slug")
    void shouldCreateStory() {
        EditorialStoryCreateRequest request = EditorialStoryCreateRequest.builder()
                .title("Oud & Amber: The Secret of Middle Eastern Attars")
                .subtitle("The sacred resin of kings")
                .excerpt("Centuries of distillation traditions in the desert...")
                .authorName("Francis Kurkdjian")
                .contentHtml("<p>Centuries of distillation traditions...</p>")
                .coverImageUrl("https://images.scentiva.com/stories/oud.webp")
                .readingTimeMinutes(8)
                .isFeatured(true)
                .tags("Oud,Amber,MiddleEast")
                .build();

        EditorialStory created = EditorialStory.builder()
                .title(request.getTitle())
                .slug("oud-amber-the-secret-of-middle-eastern-attars")
                .authorName(request.getAuthorName())
                .contentHtml(request.getContentHtml())
                .coverImageUrl(request.getCoverImageUrl())
                .readingTimeMinutes(request.getReadingTimeMinutes())
                .isFeatured(true)
                .publishedAt(LocalDateTime.now())
                .build();

        when(storyRepository.findBySlugAndIsDeletedFalse(anyString())).thenReturn(Optional.empty());
        when(storyRepository.save(any(EditorialStory.class))).thenReturn(created);

        EditorialStoryResponse response = storyService.createStory(request);

        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo("Oud & Amber: The Secret of Middle Eastern Attars");
        verify(storyRepository, times(1)).save(any(EditorialStory.class));
    }

    @Test
    @DisplayName("deleteStory should soft-delete story by ID")
    void shouldDeleteStory() {
        when(storyRepository.findById(1L)).thenReturn(Optional.of(sampleStory));
        when(storyRepository.save(any(EditorialStory.class))).thenReturn(sampleStory);

        storyService.deleteStory(1L);

        verify(storyRepository, times(1)).save(sampleStory);
        assertThat(sampleStory.isDeleted()).isTrue();
    }
}
