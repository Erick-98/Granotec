package com.granotec.inventory_api.role.dto;

import jakarta.validation.constraints.NotBlank;

public record RoleResponse(
        @NotBlank
        Integer id,
        @NotBlank
        String name
) {
}
