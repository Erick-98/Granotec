package com.granotec.inventory_api.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiResponse <T>{

    private String mensaje;
    private T data;
}
