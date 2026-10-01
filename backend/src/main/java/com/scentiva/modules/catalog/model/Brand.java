package com.scentiva.modules.catalog.model;

import com.scentiva.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * Luxury Fragrance Brand / Perfumery Maison Entity.
 */
@Entity
@Table(name = "brands")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Brand extends BaseEntity {

    @Column(name = "name", nullable = false, unique = true, length = 150)
    private String name;

    @Column(name = "slug", nullable = false, unique = true, length = 150)
    private String slug;

    @Column(name = "origin_country", nullable = false, length = 100)
    private String originCountry;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "logo_url", length = 500)
    private String logoUrl;

    @Column(name = "cover_image_url", length = 500)
    private String coverImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "tier", nullable = false, length = 50)
    @Builder.Default
    private BrandTier tier = BrandTier.PRESTIGE;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean isActive = true;
}
