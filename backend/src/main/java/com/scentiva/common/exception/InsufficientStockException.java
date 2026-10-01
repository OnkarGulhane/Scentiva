package com.scentiva.common.exception;

import org.springframework.http.HttpStatus;

public class InsufficientStockException extends BaseAppException {
    public InsufficientStockException(String sku, int requested, int available) {
        super(String.format("Insufficient stock for SKU '%s'. Requested: %d, Available: %d", sku, requested, available),
                HttpStatus.UNPROCESSABLE_ENTITY, "INSUFFICIENT_STOCK");
    }
}
