package com.granotec.inventory_api.auth.controller;

public record ResetPasswordRequest(
        String token,
        String newPassword
) { }

