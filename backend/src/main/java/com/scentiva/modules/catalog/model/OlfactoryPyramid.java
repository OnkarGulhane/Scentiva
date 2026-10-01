package com.scentiva.modules.catalog.model;

import com.scentiva.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * Fragrance Olfactory Pyramid (Top, Heart, Base Notes & Longevity).
 */
@Entity
@Table(name = "olfactory_pyramids")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OlfactoryPyramid extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false, unique = true)
    private Product product;

    @Column(name = "fragrance_family", nullable = false, length = 100)
    private String fragranceFamily;

    @Column(name = "top_notes", nullable = false, length = 500)
    private String topNotes;

    @Column(name = "heart_notes", nullable = false, length = 500)
    private String heartNotes;

    @Column(name = "base_notes", nullable = false, length = 500)
    private String baseNotes;

    @Column(name = "sillage_rating", length = 50)
    @Builder.Default
    private String sillageRating = "MODERATE";

    @Column(name = "longevity_hours")
    @Builder.Default
    private Integer longevityHours = 8;
}
