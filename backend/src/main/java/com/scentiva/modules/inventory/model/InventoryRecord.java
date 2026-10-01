package com.scentiva.modules.inventory.model;

import com.scentiva.common.entity.BaseEntity;
import com.scentiva.common.exception.InsufficientStockException;
import com.scentiva.modules.catalog.model.ProductVariant;
import jakarta.persistence.*;
import lombok.*;

/**
 * Multi-Location Inventory Record for a Product Variant in a Specific Warehouse.
 */
@Entity
@Table(name = "inventory_records", uniqueConstraints = {
        @UniqueConstraint(name = "uq_variant_warehouse", columnNames = {"variant_id", "warehouse_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryRecord extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "variant_id", nullable = false)
    private ProductVariant variant;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @Column(name = "quantity_on_hand", nullable = false)
    @Builder.Default
    private Integer quantityOnHand = 0;

    @Column(name = "quantity_reserved", nullable = false)
    @Builder.Default
    private Integer quantityReserved = 0;

    @Column(name = "low_stock_threshold", nullable = false)
    @Builder.Default
    private Integer lowStockThreshold = 5;

    public int getAvailableQuantity() {
        return Math.max(0, quantityOnHand - quantityReserved);
    }

    public boolean isLowStock() {
        return getAvailableQuantity() <= lowStockThreshold;
    }

    public void reserve(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Reservation quantity must be positive");
        }
        if (getAvailableQuantity() < quantity) {
            String sku = (variant != null) ? variant.getSku() : "UNKNOWN";
            throw new InsufficientStockException(sku, quantity, getAvailableQuantity());
        }
        this.quantityReserved += quantity;
    }

    public void releaseReservation(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Release quantity must be positive");
        }
        this.quantityReserved = Math.max(0, this.quantityReserved - quantity);
    }

    public void deduct(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Deduct quantity must be positive");
        }
        if (this.quantityOnHand < quantity) {
            String sku = (variant != null) ? variant.getSku() : "UNKNOWN";
            throw new InsufficientStockException(sku, quantity, this.quantityOnHand);
        }
        this.quantityOnHand -= quantity;
        this.quantityReserved = Math.max(0, this.quantityReserved - quantity);
    }

    public void addStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Added stock quantity must be positive");
        }
        this.quantityOnHand += quantity;
    }
}
