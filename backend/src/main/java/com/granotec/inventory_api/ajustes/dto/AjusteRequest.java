package com.granotec.inventory_api.ajustes.dto;

import lombok.Data;
import java.math.BigDecimal;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

@Data
public class AjusteRequest {
    @NotNull
    private Long almacenId;
    @NotNull
    private Integer productoId;
    private Integer loteId; // Opcional
    @NotNull
    @DecimalMin(value = "0.0001", message = "La cantidad debe ser mayor a cero")
    private BigDecimal cantidad;
    @NotBlank
    private String tipoAjuste; // POSITIVO o NEGATIVO
    @NotBlank
    private String motivo;
    @NotNull
    private Long usuarioId;
}
