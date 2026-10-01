package com.scentiva.modules.admin.dto;

import com.scentiva.modules.auth.model.UserStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserStatusUpdateRequest {
    @NotNull(message = "User status is required")
    private UserStatus status;
}
