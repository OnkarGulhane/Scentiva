package com.scentiva.modules.catalog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Create or Update Category Payload")
public class CategoryCreateRequest {

    @NotBlank(message = "Category name is required")
    @Size(max = 150, message = "Category name cannot exceed 150 characters")
    private String name;

    @NotBlank(message = "Category slug is required")
    @Size(max = 150, message = "Category slug cannot exceed 150 characters")
    private String slug;

    private String description;
    private String imageUrl;

    @Builder.Default
    private int displayOrder = 0;

    @Builder.Default
    private boolean isActive = true;
}
