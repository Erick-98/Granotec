package com.granotec.inventory_api.transportation_assignment.car.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CarResponse {
    private Long id;
    private String placa;
    private String marca;
    private Integer carrierId;
}

