package com.granotec.inventory_api.permission.dto;

import jakarta.validation.constraints.NotBlank;

public record PermissionResponse(
        @NotBlank
        Integer id,
        @NotBlank
        String name
) {
}
