package com.scentiva.modules.shipping.model;

import com.scentiva.common.entity.BaseEntity;
import com.scentiva.modules.order.model.Order;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "shipments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Shipment extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false, length = 50)
    @Builder.Default
    private ShippingProviderType provider = ShippingProviderType.MANUAL;

    @Column(name = "carrier_name", length = 100)
    private String carrierName;

    @Column(name = "tracking_number", length = 150)
    private String trackingNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    @Builder.Default
    private ShipmentStatus status = ShipmentStatus.PENDING;

    @Column(name = "dispatched_at")
    private LocalDateTime dispatchedAt;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    @OneToMany(mappedBy = "shipment", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("eventTimestamp ASC")
    @Builder.Default
    private List<ShipmentEvent> events = new ArrayList<>();

    public void addEvent(ShipmentEvent event) {
        events.add(event);
        event.setShipment(this);
    }
}
