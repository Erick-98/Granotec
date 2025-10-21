package com.granotec.inventory_api.auth.controller;

import jakarta.validation.constraints.Size;

public record RegisterRequest (
        String name,
        String email,
        @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
        String password
){ }
