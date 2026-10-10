package com.scentiva.modules.shipping.dto.shiprocket;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShiprocketCreateOrderRequest {

    @JsonProperty("order_id")
    private String orderId;

    @JsonProperty("order_date")
    private String orderDate;

    @JsonProperty("pickup_location")
    private String pickupLocation;

    @JsonProperty("channel_id")
    @Builder.Default
    private String channelId = "";

    @JsonProperty("comment")
    private String comment;

    @JsonProperty("billing_customer_name")
    private String billingCustomerName;

    @JsonProperty("billing_last_name")
    private String billingLastName;

    @JsonProperty("billing_address")
    private String billingAddress;

    @JsonProperty("billing_city")
    private String billingCity;

    @JsonProperty("billing_pincode")
    private String billingPincode;

    @JsonProperty("billing_state")
    private String billingState;

    @JsonProperty("billing_country")
    @Builder.Default
    private String billingCountry = "India";

    @JsonProperty("billing_email")
    private String billingEmail;

    @JsonProperty("billing_phone")
    private String billingPhone;

    @JsonProperty("shipping_is_billing")
    @Builder.Default
    private boolean shippingIsBilling = true;

    @JsonProperty("order_items")
    private List<ShiprocketOrderItem> orderItems;

    @JsonProperty("payment_method")
    @Builder.Default
    private String paymentMethod = "Prepaid";

    @JsonProperty("shipping_charges")
    @Builder.Default
    private double shippingCharges = 0;

    @JsonProperty("giftwrap_charges")
    @Builder.Default
    private double giftwrapCharges = 0;

    @JsonProperty("transaction_charges")
    @Builder.Default
    private double transactionCharges = 0;

    @JsonProperty("total_discount")
    @Builder.Default
    private double totalDiscount = 0;

    @JsonProperty("sub_total")
    private double subTotal;

    @JsonProperty("length")
    private int length;

    @JsonProperty("breadth")
    private int breadth;

    @JsonProperty("height")
    private int height;

    @JsonProperty("weight")
    private double weight;
}
