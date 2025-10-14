package com.granotec.inventory_api.auth.controller;

public record RegisterRequest (
        String name,
        String email,
        String password
){ }
