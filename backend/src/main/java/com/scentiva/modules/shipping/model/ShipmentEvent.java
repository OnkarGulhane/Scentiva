package com.scentiva.modules.shipping.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "shipment_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShipmentEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "shipment_id", nullable = false)
    private Shipment shipment;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private ShipmentStatus status;

    @Column(name = "location", length = 150)
    private String location;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "event_timestamp", nullable = false)
    @Builder.Default
    private LocalDateTime eventTimestamp = LocalDateTime.now();
}
