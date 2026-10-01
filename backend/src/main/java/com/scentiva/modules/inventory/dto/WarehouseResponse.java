package com.scentiva.modules.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseResponse {
    private Long id;
    private String code;
    private String name;
    private String city;
    private String state;
    private Boolean isActive;
    private LocalDateTime createdAt;
}
