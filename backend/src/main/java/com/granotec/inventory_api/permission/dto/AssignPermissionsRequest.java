package com.granotec.inventory_api.permission.dto;


import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record AssignPermissionsRequest(
        @NotEmpty
        List<String> permissions
) {
}
