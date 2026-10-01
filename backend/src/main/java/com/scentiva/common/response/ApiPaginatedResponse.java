package com.scentiva.common.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Standard Paginated Response Envelope for SCENTIVA Collections.
 *
 * @param <T> Item Type
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Standard Paginated Collection Response")
public class ApiPaginatedResponse<T> {

    @Schema(description = "List of items in the current page")
    private List<T> items;

    @Schema(description = "Current page index (0-based)", example = "0")
    private int page;

    @Schema(description = "Page size", example = "20")
    private int size;

    @Schema(description = "Total elements across all pages", example = "142")
    private long totalElements;

    @Schema(description = "Total number of pages", example = "8")
    private int totalPages;

    @Schema(description = "Is this the first page?", example = "true")
    private boolean first;

    @Schema(description = "Is this the last page?", example = "false")
    private boolean last;

    public static <T> ApiPaginatedResponse<T> of(List<T> items, int page, int size, long totalElements) {
        int totalPages = size == 0 ? 1 : (int) Math.ceil((double) totalElements / (double) size);
        return ApiPaginatedResponse.<T>builder()
                .items(items)
                .page(page)
                .size(size)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .first(page == 0)
                .last(page >= totalPages - 1)
                .build();
    }
}
