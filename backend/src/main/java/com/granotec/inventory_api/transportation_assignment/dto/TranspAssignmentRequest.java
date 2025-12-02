package com.granotec.inventory_api.transportation_assignment.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TranspAssignmentRequest {
    @NotNull(message = "carrierId es requerido")
    private Integer carrierId;
    @NotNull(message = "driverId es requerido")
    private Long driverId;
    @NotNull(message = "carId es requerido")
    private Long carId;
}
