package com.scentiva.common.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Standardized Error Envelope Response.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Standard Error Envelope Response")
public class ApiErrorResponse {

    @Schema(description = "Always false for errors", example = "false")
    @Builder.Default
    private boolean success = false;

    @Schema(description = "HTTP Status Code", example = "400")
    private int statusCode;

    @Schema(description = "Standardized error classification code", example = "VALIDATION_FAILED")
    private String errorCode;

    @Schema(description = "Human-readable error description", example = "The requested payload has validation violations")
    private String message;

    @Schema(description = "Specific field-level validation errors")
    private Map<String, List<String>> validationErrors;

    @Schema(description = "Request path that caused the error", example = "/api/v1/orders")
    private String path;

    @Schema(description = "UTC Error Timestamp")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
}
