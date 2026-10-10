package com.scentiva.modules.shipping.dto.shiprocket;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShiprocketWebhookPayload {

    private String awb;

    @JsonProperty("courier_name")
    private String courierName;

    @JsonProperty("current_status")
    private String currentStatus;

    @JsonProperty("current_status_id")
    private Integer currentStatusId;

    @JsonProperty("shipment_id")
    private Long shipmentId;

    @JsonProperty("order_id")
    private String orderId;

    private String location;

    private List<ScanActivity> scans;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ScanActivity {
        private String date;
        private String activity;
        private String location;
    }
}
