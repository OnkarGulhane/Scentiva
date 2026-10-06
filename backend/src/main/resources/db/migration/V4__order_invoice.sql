-- ==============================================================================
-- Flyway Migration: V4__order_invoice.sql
-- Module: Order Management & Financial Invoicing
-- Adds persistent invoice metadata to the orders table
-- ==============================================================================

ALTER TABLE orders
    ADD COLUMN IF NOT EXISTS invoice_number VARCHAR(100) UNIQUE,
    ADD COLUMN IF NOT EXISTS invoice_generated_at TIMESTAMP WITHOUT TIME ZONE,
    ADD COLUMN IF NOT EXISTS invoice_status VARCHAR(50) DEFAULT 'ISSUED';

CREATE INDEX IF NOT EXISTS idx_orders_invoice_number ON orders(invoice_number);
