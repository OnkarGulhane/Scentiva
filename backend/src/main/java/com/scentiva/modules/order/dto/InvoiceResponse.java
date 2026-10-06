package com.scentiva.modules.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceResponse {

    @Schema(description = "Unique Sequential Invoice Number", example = "INV-2026-000001")
    private String invoiceNumber;

    @Schema(description = "Associated Order Number", example = "SC-2026-8F4DE4B0")
    private String orderNumber;

    @Schema(description = "Invoice Generation Timestamp", example = "2026-10-07T00:15:00")
    private LocalDateTime invoiceDate;

    @Schema(description = "Order Placement Timestamp", example = "2026-10-07T00:14:30")
    private LocalDateTime orderDate;

    @Schema(description = "Invoice Status", example = "ISSUED")
    private String invoiceStatus;

    @Schema(description = "Order Status", example = "CONFIRMED")
    private String orderStatus;

    // --- Store / Seller Information ---
    @Schema(description = "Registered Company Name", example = "SCENTIVA Haute Parfumerie Private Limited")
    private String companyName;

    @Schema(description = "Company Tagline", example = "Haute Parfumerie & Luxury Fragrance Atelier")
    private String brandTagline;

    @Schema(description = "Registered Office Address", example = "Aura Prestige Towers, Baner, Pune 411045, India")
    private String registeredAddress;

    @Schema(description = "Customer Support Email", example = "concierge@scentiva.com")
    private String supportEmail;

    @Schema(description = "Customer Support Phone", example = "+91 20 4911 2026")
    private String supportPhone;

    @Schema(description = "Official Website URL", example = "https://scentiva.luxury")
    private String website;

    @Schema(description = "GSTIN / Tax ID", example = "27AALCS9812K1Z0")
    private String taxId;

    // --- Customer Information ---
    @Schema(description = "Customer Full Name", example = "Omkar Gulhane")
    private String customerName;

    @Schema(description = "Customer Registered Email", example = "omkar288113@gmail.com")
    private String customerEmail;

    @Schema(description = "Customer Phone Number", example = "+91 9876543210")
    private String customerPhone;

    // --- Addresses ---
    @Schema(description = "Shipping Address Destination")
    private String shippingAddress;

    @Schema(description = "Billing Address Destination")
    private String billingAddress;

    // --- Financial Matrix ---
    @Schema(description = "Items Gross Subtotal", example = "18500.00")
    private BigDecimal subtotal;

    @Schema(description = "Applied Promotional / Coupon Discount", example = "0.00")
    private BigDecimal discountAmount;

    @Schema(description = "Luxury Courier Delivery Fee", example = "0.00")
    private BigDecimal deliveryFee;

    @Schema(description = "Applicable Tax / GST Amount", example = "0.00")
    private BigDecimal taxAmount;

    @Schema(description = "Grand Total Amount", example = "18500.00")
    private BigDecimal grandTotal;

    @Schema(description = "Currency Code", example = "INR")
    private String currency;

    // --- Payment & Transaction Audit ---
    @Schema(description = "Payment Provider", example = "RAZORPAY")
    private String paymentProvider;

    @Schema(description = "Payment Method", example = "UPI")
    private String paymentMethod;

    @Schema(description = "Payment Status", example = "SUCCESS")
    private String paymentStatus;

    @Schema(description = "Razorpay Gateway Order ID", example = "order_OXXXXX12345")
    private String razorpayOrderId;

    @Schema(description = "Razorpay Gateway Payment ID", example = "pay_PXXXXX67890")
    private String razorpayPaymentId;

    // --- Line Items ---
    @Schema(description = "Purchased Product Line Items")
    private List<InvoiceItemResponse> items;
}
