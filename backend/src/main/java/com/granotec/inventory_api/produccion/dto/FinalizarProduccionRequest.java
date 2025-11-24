package com.granotec.inventory_api.produccion.dto;

import lombok.Data;
import java.math.BigDecimal;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;

@Data
public class FinalizarProduccionRequest {
    @NotNull
    private Integer ordenProduccionId;
    @NotNull
    @DecimalMin(value = "0.0001", message = "La cantidad debe ser mayor a cero")
    private BigDecimal cantidadProducida;
    @NotNull
    @DecimalMin(value = "0.0001", message = "El costo total debe ser mayor a cero")
    private BigDecimal costoTotal;
    @NotNull
    private Long usuarioId;
}
