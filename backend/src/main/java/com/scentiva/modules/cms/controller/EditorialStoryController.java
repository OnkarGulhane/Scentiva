package com.scentiva.modules.cms.controller;

import com.scentiva.common.response.ApiPaginatedResponse;
import com.scentiva.common.response.ApiResponse;
import com.scentiva.modules.cms.dto.EditorialStoryCreateRequest;
import com.scentiva.modules.cms.dto.EditorialStoryResponse;
import com.scentiva.modules.cms.service.CmsStoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/stories")
@RequiredArgsConstructor
@Tag(name = "Editorial Stories & Olfactory Journal CMS", description = "Haute Parfumerie articles, perfumer interviews, craftsmanship stories and editorial narratives")
public class EditorialStoryController {

    private final CmsStoryService storyService;

    @GetMapping
    @Operation(summary = "Get published editorial stories", description = "Retrieves paginated journal articles.")
    public ResponseEntity<ApiPaginatedResponse<EditorialStoryResponse>> getPublishedStories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "publishedAt"));
        ApiPaginatedResponse<EditorialStoryResponse> response = storyService.getPublishedStories(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/featured")
    @Operation(summary = "Get featured editorial stories", description = "Retrieves pinned hero stories for homepage spotlight.")
    public ResponseEntity<ApiResponse<List<EditorialStoryResponse>>> getFeaturedStories() {
        List<EditorialStoryResponse> stories = storyService.getFeaturedStories();
        return ResponseEntity.ok(ApiResponse.ok(stories, "Featured stories retrieved"));
    }

    @GetMapping("/{slug}")
    @Operation(summary = "Get editorial story by slug", description = "Retrieves full HTML article content and metadata by slug.")
    public ResponseEntity<ApiResponse<EditorialStoryResponse>> getStoryBySlug(@PathVariable String slug) {
        EditorialStoryResponse story = storyService.getStoryBySlug(slug);
        return ResponseEntity.ok(ApiResponse.ok(story, "Story retrieved successfully"));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'MARKETING_MANAGER')")
    @Operation(summary = "Publish editorial story (Admin)", description = "Creates and publishes a new olfactory journal article.")
    public ResponseEntity<ApiResponse<EditorialStoryResponse>> createStory(
            @Valid @RequestBody EditorialStoryCreateRequest request) {
        EditorialStoryResponse story = storyService.createStory(request);
        return ResponseEntity.ok(ApiResponse.ok(story, "Story published successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'MARKETING_MANAGER')")
    @Operation(summary = "Delete editorial story (Admin)", description = "Soft deletes a journal article.")
    public ResponseEntity<ApiResponse<Void>> deleteStory(@PathVariable Long id) {
        storyService.deleteStory(id);
        return ResponseEntity.ok(ApiResponse.ok(null, "Story deleted successfully"));
    }
}
