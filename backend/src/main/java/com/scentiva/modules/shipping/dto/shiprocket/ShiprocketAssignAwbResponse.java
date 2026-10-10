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
public class ShiprocketAssignAwbResponse {

    private ResponseData response;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ResponseData {
        private AwbData data;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AwbData {
        @JsonProperty("awb_code")
        private String awbCode;

        @JsonProperty("courier_company_id")
        private String courierCompanyId;

        @JsonProperty("courier_name")
        private String courierName;

        @JsonProperty("applied_weight")
        private String appliedWeight;
    }

    public String getAwbCode() {
        if (response != null && response.getData() != null) {
            return response.getData().getAwbCode();
        }
        return null;
    }

    public String getCourierName() {
        if (response != null && response.getData() != null) {
            return response.getData().getCourierName();
        }
        return null;
    }
}
