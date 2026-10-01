package com.scentiva.modules.order.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "order_snapshots")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    @Column(name = "customer_snapshot_json", nullable = false, columnDefinition = "TEXT")
    private String customerSnapshotJson;

    @Column(name = "shipping_address_snapshot_json", nullable = false, columnDefinition = "TEXT")
    private String shippingAddressSnapshotJson;

    @Column(name = "pricing_matrix_snapshot_json", nullable = false, columnDefinition = "TEXT")
    private String pricingMatrixSnapshotJson;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
