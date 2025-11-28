package com.granotec.inventory_api.produccion.dto;

import lombok.Data;
import java.math.BigDecimal;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;

@Data
public class ConsumoInsumoRequest {
    @NotNull
    private Integer ordenProduccionId;
    @NotNull
    private Integer insumoId;
    @NotNull
    @DecimalMin(value = "0.0001", message = "La cantidad debe ser mayor a cero")
    private BigDecimal cantidad;
    @NotNull
    private Integer usuarioId;
}
