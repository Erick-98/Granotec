package com.granotec.inventory_api.permission.dto;

import jakarta.validation.constraints.NotBlank;

public record AssignRoleRequest(
        @NotBlank
        String role
) {
}
