package com.granotec.inventory_api.role.dto;

import jakarta.validation.constraints.NotBlank;

public record RoleRequest(
        @NotBlank(message = "El nombre es obligatorio")
        String name
) {
}
