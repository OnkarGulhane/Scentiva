package com.scentiva.modules.order.service;

import com.scentiva.modules.order.dto.InvoiceResponse;
import com.scentiva.modules.order.model.Order;

public interface InvoiceService {

    /**
     * Retrieves the structured invoice data for an order after verifying authorization.
     * Generates persistent invoice number idempotently if not already created.
     *
     * @param userEmail Authenticated customer email or null if admin
     * @param orderNumber Unique order reference number
     * @return Full structured invoice details
     */
    InvoiceResponse getOrderInvoice(String userEmail, String orderNumber);

    /**
     * Generates and returns the binary PDF representation of an order's invoice.
     *
     * @param userEmail Authenticated customer email or null if admin
     * @param orderNumber Unique order reference number
     * @return byte array containing the A4 PDF invoice
     */
    byte[] getOrderInvoicePdf(String userEmail, String orderNumber);

    /**
     * Idempotently creates or updates the persistent invoice metadata for a confirmed order.
     *
     * @param order Confirmed order entity
     * @return Generated/existing InvoiceResponse
     */
    InvoiceResponse generateInvoiceForOrder(Order order);
}
