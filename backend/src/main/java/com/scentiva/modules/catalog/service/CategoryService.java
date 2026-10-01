package com.scentiva.modules.catalog.service;

import com.scentiva.modules.catalog.dto.CategoryCreateRequest;
import com.scentiva.modules.catalog.dto.CategoryResponse;

import java.util.List;

public interface CategoryService {

    List<CategoryResponse> getAllActiveCategories();

    CategoryResponse getCategoryBySlug(String slug);

    CategoryResponse getCategoryById(Long id);

    CategoryResponse createCategory(CategoryCreateRequest request);

    CategoryResponse updateCategory(Long id, CategoryCreateRequest request);

    void deleteCategory(Long id);
}
