package com.granotec.inventory_api.user.dto;

import com.granotec.inventory_api.role.dto.RoleResponse;

public record UserResponse (
        Integer id,
        String name,
        String email,
        RoleResponse role
){ }
