package com.scentiva.modules.order.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.scentiva.modules.order.dto.InvoiceItemResponse;
import com.scentiva.modules.order.dto.InvoiceResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;

/**
 * High-precision luxury PDF invoice generator for SCENTIVA orders.
 * Renders vector-sharp, multi-page compliant A4 invoices matching brand tokens.
 */
@Slf4j
@Component
public class PdfInvoiceGenerator {

    private static final Color COLOR_PLUM_DARK = new Color(50, 16, 39);      // #321027
    private static final Color COLOR_PLUM_MED = new Color(69, 19, 51);       // #451333
    private static final Color COLOR_GOLD = new Color(199, 166, 106);        // #C7A66A
    private static final Color COLOR_BG_LIGHT = new Color(250, 248, 247);    // #FAF8F7
    private static final Color COLOR_BORDER = new Color(228, 220, 218);      // #E4DCDA
    private static final Color COLOR_TEXT_DARK = new Color(29, 23, 27);      // #1D171B
    private static final Color COLOR_TEXT_MUTED = new Color(122, 110, 117);  // #7A6E75
    private static final Color COLOR_SUCCESS_BG = new Color(230, 247, 235);
    private static final Color COLOR_SUCCESS_TEXT = new Color(34, 139, 34);

    private static final DecimalFormat CURRENCY_FMT = new DecimalFormat("#,##0.00");
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm");

    public byte[] generateInvoicePdf(InvoiceResponse invoice) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4, 36, 36, 36, 36);
            PdfWriter writer = PdfWriter.getInstance(document, baos);

            // Add Header/Footer Event for page numbers
            writer.setPageEvent(new PdfPageEventHelper() {
                @Override
                public void onEndPage(PdfWriter writer, Document document) {
                    PdfContentByte cb = writer.getDirectContent();
                    Font footerFont = FontFactory.getFont(FontFactory.HELVETICA, 8, COLOR_TEXT_MUTED);
                    Phrase footer = new Phrase(
                            "SCENTIVA Haute Parfumerie • " + invoice.getInvoiceNumber() + " • Page " + writer.getPageNumber(),
                            footerFont
                    );
                    ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, footer,
                            (document.right() - document.left()) / 2 + document.leftMargin(),
                            document.bottom() - 15, 0);
                }
            });

            document.open();

            // 1. BRAND HEADER BANNER
            PdfPTable headerTable = new PdfPTable(2);
            headerTable.setWidthPercentage(100);
            headerTable.setWidths(new float[]{60, 40});
            headerTable.setSpacingAfter(15f);

            PdfPCell brandCell = new PdfPCell();
            brandCell.setBackgroundColor(COLOR_PLUM_DARK);
            brandCell.setPadding(16);
            brandCell.setBorder(Rectangle.NO_BORDER);

            Font brandFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, COLOR_GOLD);
            Paragraph brandTitle = new Paragraph("SCENTIVA", brandFont);
            brandTitle.setSpacingAfter(2);
            brandCell.addElement(brandTitle);

            Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA, 8, new Color(233, 183, 216));
            Paragraph brandSubtitle = new Paragraph("HAUTE PARFUMERIE • ATELIER PRIVÉ", subtitleFont);
            brandCell.addElement(brandSubtitle);

            headerTable.addCell(brandCell);

            PdfPCell invoiceMetaCell = new PdfPCell();
            invoiceMetaCell.setBackgroundColor(COLOR_PLUM_MED);
            invoiceMetaCell.setPadding(16);
            invoiceMetaCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            invoiceMetaCell.setBorder(Rectangle.NO_BORDER);

            Font invTitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.WHITE);
            Paragraph invTitle = new Paragraph("TAX INVOICE", invTitleFont);
            invTitle.setAlignment(Element.ALIGN_RIGHT);
            invTitle.setSpacingAfter(4);
            invoiceMetaCell.addElement(invTitle);

            Font invNoFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, COLOR_GOLD);
            Paragraph invNo = new Paragraph(invoice.getInvoiceNumber(), invNoFont);
            invNo.setAlignment(Element.ALIGN_RIGHT);
            invoiceMetaCell.addElement(invNo);

            headerTable.addCell(invoiceMetaCell);
            document.add(headerTable);

            // 2. STORE INFO & INVOICE METADATA TWO-COLUMN
            PdfPTable metaGrid = new PdfPTable(2);
            metaGrid.setWidthPercentage(100);
            metaGrid.setWidths(new float[]{50, 50});
            metaGrid.setSpacingAfter(15f);

            // Left: Seller / Store Details
            PdfPCell sellerCell = new PdfPCell();
            sellerCell.setBorderColor(COLOR_BORDER);
            sellerCell.setBackgroundColor(COLOR_BG_LIGHT);
            sellerCell.setPadding(12);

            Font sectionHeaderFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, COLOR_PLUM_DARK);
            Paragraph sellerHead = new Paragraph("SOLD BY (SELLER):", sectionHeaderFont);
            sellerHead.setSpacingAfter(4);
            sellerCell.addElement(sellerHead);

            Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 8, COLOR_TEXT_DARK);
            Font boldBodyFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, COLOR_TEXT_DARK);

            sellerCell.addElement(new Paragraph(invoice.getCompanyName(), boldBodyFont));
            sellerCell.addElement(new Paragraph(invoice.getRegisteredAddress(), bodyFont));
            sellerCell.addElement(new Paragraph("GSTIN / Tax ID: " + invoice.getTaxId(), boldBodyFont));
            sellerCell.addElement(new Paragraph("Email: " + invoice.getSupportEmail() + " | Phone: " + invoice.getSupportPhone(), bodyFont));

            metaGrid.addCell(sellerCell);

            // Right: Order & Payment Status Details
            PdfPCell orderMetaCell = new PdfPCell();
            orderMetaCell.setBorderColor(COLOR_BORDER);
            orderMetaCell.setBackgroundColor(COLOR_BG_LIGHT);
            orderMetaCell.setPadding(12);

            Paragraph orderHead = new Paragraph("ORDER & INVOICE DETAILS:", sectionHeaderFont);
            orderHead.setSpacingAfter(4);
            orderMetaCell.addElement(orderHead);

            orderMetaCell.addElement(new Paragraph("Order Number: " + invoice.getOrderNumber(), boldBodyFont));
            if (invoice.getInvoiceDate() != null) {
                orderMetaCell.addElement(new Paragraph("Invoice Date: " + invoice.getInvoiceDate().format(DATE_FMT), bodyFont));
            }
            if (invoice.getOrderDate() != null) {
                orderMetaCell.addElement(new Paragraph("Order Date: " + invoice.getOrderDate().format(DATE_FMT), bodyFont));
            }
            orderMetaCell.addElement(new Paragraph("Payment Method: " + (invoice.getPaymentMethod() != null ? invoice.getPaymentMethod() : "RAZORPAY"), bodyFont));
            orderMetaCell.addElement(new Paragraph("Payment Status: " + invoice.getPaymentStatus() + " (PAID)", boldBodyFont));

            if (invoice.getRazorpayPaymentId() != null && !invoice.getRazorpayPaymentId().isBlank()) {
                orderMetaCell.addElement(new Paragraph("Gateway Txn ID: " + invoice.getRazorpayPaymentId(), bodyFont));
            }

            metaGrid.addCell(orderMetaCell);
            document.add(metaGrid);

            // 3. CUSTOMER BILL TO & SHIP TO
            PdfPTable customerGrid = new PdfPTable(2);
            customerGrid.setWidthPercentage(100);
            customerGrid.setWidths(new float[]{50, 50});
            customerGrid.setSpacingAfter(15f);

            PdfPCell billToCell = new PdfPCell();
            billToCell.setBorderColor(COLOR_BORDER);
            billToCell.setPadding(10);
            billToCell.addElement(new Paragraph("BILLED TO:", sectionHeaderFont));
            billToCell.addElement(new Paragraph(invoice.getCustomerName(), boldBodyFont));
            billToCell.addElement(new Paragraph("Email: " + invoice.getCustomerEmail(), bodyFont));
            if (invoice.getCustomerPhone() != null && !invoice.getCustomerPhone().isBlank()) {
                billToCell.addElement(new Paragraph("Phone: " + invoice.getCustomerPhone(), bodyFont));
            }
            if (invoice.getBillingAddress() != null && !invoice.getBillingAddress().isBlank()) {
                billToCell.addElement(new Paragraph(invoice.getBillingAddress(), bodyFont));
            }

            customerGrid.addCell(billToCell);

            PdfPCell shipToCell = new PdfPCell();
            shipToCell.setBorderColor(COLOR_BORDER);
            shipToCell.setPadding(10);
            shipToCell.addElement(new Paragraph("SHIPPED TO:", sectionHeaderFont));
            shipToCell.addElement(new Paragraph(invoice.getCustomerName(), boldBodyFont));
            if (invoice.getShippingAddress() != null && !invoice.getShippingAddress().isBlank()) {
                shipToCell.addElement(new Paragraph(invoice.getShippingAddress(), bodyFont));
            } else {
                shipToCell.addElement(new Paragraph("Standard Priority Delivery", bodyFont));
            }

            customerGrid.addCell(shipToCell);
            document.add(customerGrid);

            // 4. ITEMS TABLE
            PdfPTable itemsTable = new PdfPTable(6);
            itemsTable.setWidthPercentage(100);
            itemsTable.setWidths(new float[]{35, 20, 10, 10, 12, 13});
            itemsTable.setHeaderRows(1);
            itemsTable.setSpacingAfter(12f);

            // Header cells
            String[] headers = {"Fragrance Item", "Brand / SKU", "Vol", "Qty", "Unit Price", "Total"};
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, COLOR_GOLD)));
                cell.setBackgroundColor(COLOR_PLUM_DARK);
                cell.setPadding(8);
                cell.setBorderColor(COLOR_PLUM_MED);
                if (h.equals("Qty") || h.equals("Unit Price") || h.equals("Total")) {
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                }
                itemsTable.addCell(cell);
            }

            // Data rows
            int idx = 0;
            if (invoice.getItems() != null) {
                for (InvoiceItemResponse item : invoice.getItems()) {
                    Color rowBg = (idx % 2 == 0) ? Color.WHITE : COLOR_BG_LIGHT;

                    PdfPCell nameCell = new PdfPCell(new Phrase(item.getProductName(), boldBodyFont));
                    nameCell.setBackgroundColor(rowBg);
                    nameCell.setPadding(7);
                    nameCell.setBorderColor(COLOR_BORDER);
                    itemsTable.addCell(nameCell);

                    String brandSku = item.getBrandName() + (item.getSku() != null ? "\n" + item.getSku() : "");
                    PdfPCell brandSkuCell = new PdfPCell(new Phrase(brandSku, bodyFont));
                    brandSkuCell.setBackgroundColor(rowBg);
                    brandSkuCell.setPadding(7);
                    brandSkuCell.setBorderColor(COLOR_BORDER);
                    itemsTable.addCell(brandSkuCell);

                    PdfPCell volCell = new PdfPCell(new Phrase(item.getVolumeMl() != null ? item.getVolumeMl() + " ml" : "-", bodyFont));
                    volCell.setBackgroundColor(rowBg);
                    volCell.setPadding(7);
                    volCell.setBorderColor(COLOR_BORDER);
                    itemsTable.addCell(volCell);

                    PdfPCell qtyCell = new PdfPCell(new Phrase(String.valueOf(item.getQuantity()), bodyFont));
                    qtyCell.setBackgroundColor(rowBg);
                    qtyCell.setPadding(7);
                    qtyCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    qtyCell.setBorderColor(COLOR_BORDER);
                    itemsTable.addCell(qtyCell);

                    String unitPriceStr = "INR " + CURRENCY_FMT.format(item.getUnitPrice());
                    PdfPCell priceCell = new PdfPCell(new Phrase(unitPriceStr, bodyFont));
                    priceCell.setBackgroundColor(rowBg);
                    priceCell.setPadding(7);
                    priceCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    priceCell.setBorderColor(COLOR_BORDER);
                    itemsTable.addCell(priceCell);

                    String totalPriceStr = "INR " + CURRENCY_FMT.format(item.getTotalPrice());
                    PdfPCell totalCell = new PdfPCell(new Phrase(totalPriceStr, boldBodyFont));
                    totalCell.setBackgroundColor(rowBg);
                    totalCell.setPadding(7);
                    totalCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    totalCell.setBorderColor(COLOR_BORDER);
                    itemsTable.addCell(totalCell);

                    idx++;
                }
            }

            document.add(itemsTable);

            // 5. FINANCIAL SUMMARY
            PdfPTable summaryTable = new PdfPTable(2);
            summaryTable.setWidthPercentage(100);
            summaryTable.setWidths(new float[]{60, 40});
            summaryTable.setSpacingAfter(15f);

            // Left note
            PdfPCell noteCell = new PdfPCell();
            noteCell.setBorder(Rectangle.NO_BORDER);
            noteCell.setPadding(10);
            noteCell.addElement(new Paragraph("AUTHENTICITY & GUARANTEE", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, COLOR_PLUM_DARK)));
            noteCell.addElement(new Paragraph(
                    "This invoice confirms genuine provenance of batch-tested haute parfumerie items stored under climate-controlled conditions in the SCENTIVA Vault.",
                    FontFactory.getFont(FontFactory.HELVETICA, 7, COLOR_TEXT_MUTED)
            ));
            summaryTable.addCell(noteCell);

            // Right breakdown
            PdfPCell mathCell = new PdfPCell();
            mathCell.setBorderColor(COLOR_BORDER);
            mathCell.setBackgroundColor(COLOR_BG_LIGHT);
            mathCell.setPadding(8);

            PdfPTable mathGrid = new PdfPTable(2);
            mathGrid.setWidthPercentage(100);

            addMathRow(mathGrid, "Items Subtotal:", "INR " + CURRENCY_FMT.format(invoice.getSubtotal()), bodyFont);
            if (invoice.getDiscountAmount() != null && invoice.getDiscountAmount().compareTo(java.math.BigDecimal.ZERO) > 0) {
                addMathRow(mathGrid, "Promotional Discount:", "- INR " + CURRENCY_FMT.format(invoice.getDiscountAmount()), bodyFont);
            }
            addMathRow(mathGrid, "Luxury Courier Delivery:", (invoice.getDeliveryFee() == null || invoice.getDeliveryFee().compareTo(java.math.BigDecimal.ZERO) == 0) ? "COMPLIMENTARY" : "INR " + CURRENCY_FMT.format(invoice.getDeliveryFee()), bodyFont);
            if (invoice.getTaxAmount() != null && invoice.getTaxAmount().compareTo(java.math.BigDecimal.ZERO) > 0) {
                addMathRow(mathGrid, "Applicable Tax / GST:", "INR " + CURRENCY_FMT.format(invoice.getTaxAmount()), bodyFont);
            }

            // Grand Total Row
            PdfPCell grandLabelCell = new PdfPCell(new Phrase("GRAND TOTAL:", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, COLOR_GOLD)));
            grandLabelCell.setBackgroundColor(COLOR_PLUM_DARK);
            grandLabelCell.setPadding(6);
            grandLabelCell.setBorder(Rectangle.NO_BORDER);
            mathGrid.addCell(grandLabelCell);

            String grandTotalStr = "INR " + CURRENCY_FMT.format(invoice.getGrandTotal());
            PdfPCell grandValCell = new PdfPCell(new Phrase(grandTotalStr, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, COLOR_GOLD)));
            grandValCell.setBackgroundColor(COLOR_PLUM_DARK);
            grandValCell.setPadding(6);
            grandValCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            grandValCell.setBorder(Rectangle.NO_BORDER);
            mathGrid.addCell(grandValCell);

            mathCell.addElement(mathGrid);
            summaryTable.addCell(mathCell);
            document.add(summaryTable);

            // 6. CLOSING FOOTER
            Paragraph signOff = new Paragraph(
                    "Thank you for choosing SCENTIVA Haute Parfumerie. This is a computer-generated tax invoice and requires no physical signature.",
                    FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 7, COLOR_TEXT_MUTED)
            );
            signOff.setAlignment(Element.ALIGN_CENTER);
            document.add(signOff);

            document.close();
            return baos.toByteArray();
        } catch (Exception ex) {
            log.error("Failed to generate PDF invoice for {}: {}", invoice.getInvoiceNumber(), ex.getMessage(), ex);
            throw new RuntimeException("PDF Invoice generation error: " + ex.getMessage(), ex);
        }
    }

    private void addMathRow(PdfPTable table, String label, String value, Font font) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, font));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setPadding(3);
        table.addCell(labelCell);

        PdfPCell valCell = new PdfPCell(new Phrase(value, font));
        valCell.setBorder(Rectangle.NO_BORDER);
        valCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        valCell.setPadding(3);
        table.addCell(valCell);
    }
}
