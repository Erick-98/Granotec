package com.granotec.inventory_api.transportation_assignment.car.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CarRequest {
    @NotBlank(message = "La placa es requerido")
    private String placa;
    private String marca;
    @NotNull(message = "carrierId es requerido")
    private Integer carrierId;
}
