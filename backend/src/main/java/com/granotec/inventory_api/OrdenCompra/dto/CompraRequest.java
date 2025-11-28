package com.granotec.inventory_api.OrdenCompra.dto;

import jakarta.validation.constraints.DecimalMin;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class CompraRequest {

    private String numero;

    @NotNull
    private Long proveedorId;
    @NotNull
    private Long almacenId;
    @NotNull
    private List<DetalleCompraDTO> detalles;

    @Data
    public static class DetalleCompraDTO {
        @NotNull
        private Long productoId;

        private String lote;
        private LocalDate fechaProduccion;
        private LocalDate fechaVencimiento;

        @NotNull
        @DecimalMin(value = "0.0001", message = "La cantidad debe ser mayor a cero")
        private BigDecimal cantidad;
        @NotNull
        @DecimalMin(value = "0.0001", message = "El precio unitario debe ser mayor a cero")
        private BigDecimal precioUnitario;
    }
}
