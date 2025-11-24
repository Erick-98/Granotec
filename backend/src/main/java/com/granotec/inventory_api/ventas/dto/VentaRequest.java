package com.granotec.inventory_api.ventas.dto;

import jakarta.validation.constraints.DecimalMin;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class VentaRequest {
    @NotNull
    private Long clienteId;
    @NotNull
    private Long vendedorId;
    @NotNull
    private Long almacenId;
    @NotNull
    private List<DetalleVentaDTO> detalles;

    @Data
    public static class DetalleVentaDTO {
        @NotNull
        private Integer productoId;
        @NotNull
        @DecimalMin(value = "0.0001", message = "La cantidad debe ser mayor a cero")
        private BigDecimal cantidad;
    }
}
