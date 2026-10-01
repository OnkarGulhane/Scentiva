package com.scentiva.modules.inventory.model;

/**
 * Inventory Movement Audit Reason Codes.
 */
public enum MovementReason {
    RECEIPT,        // New stock received from manufacturer/distributor
    SALE,           // Deducted upon order fulfillment
    RESERVATION,    // Temporary hold during checkout
    RELEASE,        // Reservation released due to timeout or cancellation
    CANCELLATION,   // Restocked after order cancellation
    RETURN,         // Restocked after inspection of returned goods
    DAMAGE,         // Damaged bottle written off
    ADJUSTMENT,     // Manual warehouse stock reconciliation
    TRANSFER,       // Inter-warehouse transfer
    CORRECTION      // Administrative stock correction
}
