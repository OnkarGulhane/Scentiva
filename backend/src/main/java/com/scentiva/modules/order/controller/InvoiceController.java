package com.scentiva.modules.order.controller;

import com.scentiva.common.response.ApiResponse;
import com.scentiva.modules.order.dto.InvoiceResponse;
import com.scentiva.modules.order.service.InvoiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "Order Invoice & Receipts", description = "Endpoints for viewing, retrieving and generating PDF tax invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    @GetMapping("/{orderNumber}/invoice")
    @Operation(summary = "Get order invoice JSON", description = "Retrieves structured invoice data for an order (authorized customer or admin).")
    public ResponseEntity<ApiResponse<InvoiceResponse>> getOrderInvoice(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String orderNumber) {
        String email = (userDetails != null) ? userDetails.getUsername() : null;
        InvoiceResponse response = invoiceService.getOrderInvoice(email, orderNumber);
        return ResponseEntity.ok(ApiResponse.ok(response, "Invoice retrieved successfully"));
    }

    @GetMapping("/{orderNumber}/invoice/pdf")
    @Operation(summary = "Download / View order invoice PDF", description = "Generates and streams high-resolution vector PDF invoice (authorized customer or admin).")
    public ResponseEntity<byte[]> getOrderInvoicePdf(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String orderNumber) {
        String email = (userDetails != null) ? userDetails.getUsername() : null;
        InvoiceResponse invoice = invoiceService.getOrderInvoice(email, orderNumber);
        byte[] pdfBytes = invoiceService.getOrderInvoicePdf(email, orderNumber);

        String filename = "Scentiva-Invoice-" + invoice.getInvoiceNumber() + ".pdf";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(org.springframework.http.ContentDisposition.inline().filename(filename).build());
        headers.setContentLength(pdfBytes.length);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }
}
