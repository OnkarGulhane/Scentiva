package com.scentiva.modules.inventory.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseCreateRequest {

    @NotBlank(message = "Warehouse code is required")
    @Size(max = 50, message = "Warehouse code must not exceed 50 characters")
    private String code;

    @NotBlank(message = "Warehouse name is required")
    @Size(max = 150, message = "Warehouse name must not exceed 150 characters")
    private String name;

    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City must not exceed 100 characters")
    private String city;

    @NotBlank(message = "State is required")
    @Size(max = 100, message = "State must not exceed 100 characters")
    private String state;

    @Builder.Default
    private Boolean isActive = true;
}
