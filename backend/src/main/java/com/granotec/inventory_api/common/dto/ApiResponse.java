package com.granotec.inventory_api.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiResponse <T>{

    private final String mensaje;
    private final T data;
}
