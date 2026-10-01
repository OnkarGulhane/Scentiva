package com.scentiva.modules.catalog.model;

import com.scentiva.common.entity.BaseEntity;
import com.scentiva.modules.inventory.model.InventoryRecord;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Sellable Product Variant (Volume, Concentration, SKU, Exact Decimal Pricing).
 */
@Entity
@Table(name = "product_variants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVariant extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "sku", nullable = false, unique = true, length = 100)
    private String sku;

    @Column(name = "volume_ml", nullable = false)
    private Integer volumeMl;

    @Enumerated(EnumType.STRING)
    @Column(name = "concentration", nullable = false, length = 50)
    @Builder.Default
    private Concentration concentration = Concentration.EDP;

    @Column(name = "base_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal basePrice;

    @Column(name = "sale_price", precision = 12, scale = 2)
    private BigDecimal salePrice;

    @Column(name = "weight_grams")
    @Builder.Default
    private Integer weightGrams = 350;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean isActive = true;

    @OneToMany(mappedBy = "variant", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<InventoryRecord> inventoryRecords = new ArrayList<>();

    public BigDecimal getEffectivePrice() {
        return (salePrice != null && salePrice.compareTo(BigDecimal.ZERO) > 0 && salePrice.compareTo(basePrice) < 0)
                ? salePrice
                : basePrice;
    }

    public int getTotalAvailableQuantity() {
        return inventoryRecords.stream()
                .mapToInt(InventoryRecord::getAvailableQuantity)
                .sum();
    }
}
