package com.granotec.inventory_api.auth.dto;

public record AuthRequest (
        String email,
        String password
){ }
