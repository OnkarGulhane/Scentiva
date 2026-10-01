package com.scentiva.modules.cms.service.impl;

import com.scentiva.common.exception.ResourceNotFoundException;
import com.scentiva.common.response.ApiPaginatedResponse;
import com.scentiva.modules.cms.dto.EditorialStoryCreateRequest;
import com.scentiva.modules.cms.dto.EditorialStoryResponse;
import com.scentiva.modules.cms.model.EditorialStory;
import com.scentiva.modules.cms.repository.EditorialStoryRepository;
import com.scentiva.modules.cms.service.CmsStoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class CmsStoryServiceImpl implements CmsStoryService {

    private final EditorialStoryRepository storyRepository;

    @Override
    @Transactional(readOnly = true)
    public ApiPaginatedResponse<EditorialStoryResponse> getPublishedStories(Pageable pageable) {
        Page<EditorialStory> page = storyRepository.findByIsDeletedFalseOrderByPublishedAtDesc(pageable);

        List<EditorialStoryResponse> items = page.getContent().stream()
                .map(this::mapToResponse)
                .toList();

        return ApiPaginatedResponse.of(items, page.getNumber(), page.getSize(), page.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EditorialStoryResponse> getFeaturedStories() {
        return storyRepository.findByIsFeaturedTrueAndIsDeletedFalseOrderByPublishedAtDesc().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public EditorialStoryResponse getStoryBySlug(String slug) {
        EditorialStory story = storyRepository.findBySlugAndIsDeletedFalse(slug)
                .orElseThrow(() -> new ResourceNotFoundException("EditorialStory", "slug", slug));

        return mapToResponse(story);
    }

    @Override
    @Transactional
    public EditorialStoryResponse createStory(EditorialStoryCreateRequest request) {
        String slug = (request.getSlug() != null && !request.getSlug().trim().isEmpty())
                ? request.getSlug().trim().toLowerCase(Locale.ROOT)
                : request.getTitle().trim().toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", "-");

        if (storyRepository.findBySlugAndIsDeletedFalse(slug).isPresent()) {
            throw new IllegalArgumentException("Editorial story with slug '" + slug + "' already exists");
        }

        LocalDateTime publishedAt = (request.getPublishedAt() != null) ? request.getPublishedAt() : LocalDateTime.now();

        EditorialStory story = EditorialStory.builder()
                .title(request.getTitle())
                .slug(slug)
                .subtitle(request.getSubtitle())
                .excerpt(request.getExcerpt())
                .contentHtml(request.getContentHtml())
                .coverImageUrl(request.getCoverImageUrl())
                .authorName(request.getAuthorName() != null ? request.getAuthorName() : "Scentiva Editorial Guild")
                .readingTimeMinutes(request.getReadingTimeMinutes() > 0 ? request.getReadingTimeMinutes() : 4)
                .isFeatured(request.isFeatured())
                .publishedAt(publishedAt)
                .tags(request.getTags())
                .build();

        story = storyRepository.save(story);
        log.info("Created editorial story id={}, title='{}', slug='{}'", story.getId(), story.getTitle(), story.getSlug());

        return mapToResponse(story);
    }

    @Override
    @Transactional
    public void deleteStory(Long id) {
        EditorialStory story = storyRepository.findById(id)
                .filter(s -> !s.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("EditorialStory", "id", id));

        story.setDeleted(true);
        storyRepository.save(story);
        log.info("Soft deleted editorial story id={}, title='{}'", story.getId(), story.getTitle());
    }

    private EditorialStoryResponse mapToResponse(EditorialStory story) {
        return EditorialStoryResponse.builder()
                .id(story.getId())
                .title(story.getTitle())
                .slug(story.getSlug())
                .subtitle(story.getSubtitle())
                .excerpt(story.getExcerpt())
                .contentHtml(story.getContentHtml())
                .coverImageUrl(story.getCoverImageUrl())
                .authorName(story.getAuthorName())
                .readingTimeMinutes(story.getReadingTimeMinutes())
                .isFeatured(story.isFeatured())
                .publishedAt(story.getPublishedAt())
                .tags(story.getTags())
                .createdAt(story.getCreatedAt())
                .build();
    }
}
