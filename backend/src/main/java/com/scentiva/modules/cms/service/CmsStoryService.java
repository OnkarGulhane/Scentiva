package com.scentiva.modules.cms.service;

import com.scentiva.common.response.ApiPaginatedResponse;
import com.scentiva.modules.cms.dto.EditorialStoryCreateRequest;
import com.scentiva.modules.cms.dto.EditorialStoryResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CmsStoryService {

    ApiPaginatedResponse<EditorialStoryResponse> getPublishedStories(Pageable pageable);

    List<EditorialStoryResponse> getFeaturedStories();

    EditorialStoryResponse getStoryBySlug(String slug);

    EditorialStoryResponse createStory(EditorialStoryCreateRequest request);

    void deleteStory(Long id);
}
