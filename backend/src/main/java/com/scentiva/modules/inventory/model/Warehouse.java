package com.scentiva.modules.inventory.model;

import com.scentiva.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * Physical Warehouse / Fulfillment Hub Entity.
 */
@Entity
@Table(name = "warehouses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Warehouse extends BaseEntity {

    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "city", nullable = false, length = 100)
    private String city;

    @Column(name = "state", nullable = false, length = 100)
    private String state;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean isActive = true;
}
