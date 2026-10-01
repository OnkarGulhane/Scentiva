package com.scentiva.modules.admin.dto;

import com.scentiva.modules.auth.model.Role;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserRoleUpdateRequest {
    @NotNull(message = "Role is required")
    private Role role;
}
