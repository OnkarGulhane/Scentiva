package com.scentiva.modules.shipping.dto.shiprocket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShiprocketLoginRequest {
    private String email;
    private String password;
}
