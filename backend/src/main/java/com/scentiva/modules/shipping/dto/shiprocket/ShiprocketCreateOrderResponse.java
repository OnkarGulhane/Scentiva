package com.scentiva.modules.shipping.dto.shiprocket;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShiprocketCreateOrderResponse {

    @JsonProperty("order_id")
    private Long orderId;

    @JsonProperty("shipment_id")
    private Long shipmentId;

    private String status;

    @JsonProperty("status_code")
    private Integer statusCode;

    @JsonProperty("awb_code")
    private String awbCode;

    @JsonProperty("courier_company_id")
    private String courierCompanyId;

    @JsonProperty("courier_name")
    private String courierName;

    private String message;
}
