package com.scentiva.modules.order.model;

import com.scentiva.common.entity.BaseEntity;
import com.scentiva.modules.customer.model.Customer;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order extends BaseEntity {

    @Column(name = "order_number", nullable = false, unique = true, length = 100)
    private String orderNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    @Builder.Default
    private OrderStatus status = OrderStatus.PLACED;

    @Column(name = "subtotal", nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "discount_amount", nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "delivery_fee", nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal deliveryFee = BigDecimal.ZERO;

    @Column(name = "tax_amount", nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "idempotency_key", unique = true, length = 100)
    private String idempotencyKey;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "invoice_number", unique = true, length = 100)
    private String invoiceNumber;

    @Column(name = "invoice_generated_at")
    private java.time.LocalDateTime invoiceGeneratedAt;

    @Column(name = "invoice_status", length = 50)
    @Builder.Default
    private String invoiceStatus = "ISSUED";

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private OrderSnapshot snapshot;

    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }

    public void setSnapshot(OrderSnapshot snapshot) {
        this.snapshot = snapshot;
        if (snapshot != null) {
            snapshot.setOrder(this);
        }
    }
}
