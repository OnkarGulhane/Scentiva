package com.scentiva.modules.catalog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Olfactory Note Pyramid DTO.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Olfactory Note Pyramid")
public class OlfactoryPyramidDto {

    @NotBlank(message = "Fragrance family is required")
    @Schema(description = "Fragrance family classification", example = "Woody Oriental")
    private String fragranceFamily;

    @NotBlank(message = "Top notes are required")
    @Schema(description = "Opening volatile notes", example = "Calabrian Bergamot, Pink Pepper")
    private String topNotes;

    @NotBlank(message = "Heart notes are required")
    @Schema(description = "Middle personality notes", example = "Sichuan Pepper, Lavender, Star Anise")
    private String heartNotes;

    @NotBlank(message = "Base notes are required")
    @Schema(description = "Dry down lingering fixatives", example = "Ambroxan, Cedarwood, Vanilla")
    private String baseNotes;

    @Schema(description = "Sillage projection rating", example = "ENORMOUS")
    private String sillageRating;

    @Schema(description = "Longevity on skin in hours", example = "10")
    private Integer longevityHours;
}
