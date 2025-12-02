package com.granotec.inventory_api.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TokenResponse (
        @JsonProperty("access_token")
        String accesToken,
        @JsonProperty("refresh_token")
        String refreshToken
){ }
