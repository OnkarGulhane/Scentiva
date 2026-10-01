package com.scentiva.modules.shipping.model;

public enum ShipmentStatus {
    PENDING,
    PROCESSING,
    DISPATCHED,
    IN_TRANSIT,
    OUT_FOR_DELIVERY,
    DELIVERED,
    FAILED_DELIVERY,
    RETURNED,
    CANCELLED
}
