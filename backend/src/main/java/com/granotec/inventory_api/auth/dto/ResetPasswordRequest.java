package com.granotec.inventory_api.auth.dto;

public record ResetPasswordRequest(
        String token,
        String newPassword
) { }

