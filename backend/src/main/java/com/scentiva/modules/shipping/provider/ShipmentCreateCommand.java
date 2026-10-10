package com.scentiva.modules.shipping.provider;

import com.scentiva.modules.shipping.dto.ShipmentItemDto;
import com.scentiva.modules.shipping.model.ShippingProviderType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentCreateCommand {
    private Long orderId;
    private String orderNumber;
    private String recipientName;
    private String recipientPhone;
    private String recipientEmail;
    private String shippingAddress;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private BigDecimal orderTotal;
    private String carrierName;
    private String customTrackingNumber;
    private String dispatchLocation;
    private List<ShipmentItemDto> items;
}
