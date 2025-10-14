package com.granotec.inventory_api.auth.controller;

public record AuthRequest (
        String email,
        String password
){ }
