package com.scentiva.modules.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailTestRequest {

    @NotBlank(message = "Recipient email address is required")
    @Email(message = "Valid email address is required")
    @Schema(description = "Recipient email address (e.g., your personal email for testing)", example = "user@example.com")
    private String to;

    @Schema(description = "Type of email template: 'welcome', 'order-confirmed', 'order-shipped', 'order-delivered', 'order-cancelled', 'password-reset'", example = "order-confirmed")
    private String templateType;

    @Schema(description = "Customer name to display in the email", example = "Omkar Gulhane")
    private String customerName;
}
