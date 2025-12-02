package com.granotec.inventory_api.permission.dto;

import jakarta.validation.constraints.NotBlank;

public record PermissionRequest(
        @NotBlank String name
) {
}
