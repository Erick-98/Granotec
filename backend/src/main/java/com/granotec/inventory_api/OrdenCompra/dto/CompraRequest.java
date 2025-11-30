package com.granotec.inventory_api.OrdenCompra.dto;

import jakarta.mail.event.MailEvent;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class CompraRequest {

    @NotNull(message = "El número de factura es obligatorio")
    private String numeroFactura;

    @NotNull(message = "El proveedor es obligatorio")
    private Long proveedorId;

    private LocalDate fecha;

    @NotNull
    private Long almacenId;

    @NotNull(message = "Debe incluir al menos un detalle de compra")
    @Valid
    private List<DetalleCompraDTO> detalles;

    @Data
    public static class DetalleCompraDTO {

        @NotNull(message = "El producto es obligatorio")
        private Integer productoId;

        @NotNull
        @DecimalMin(value = "0.0001", message = "La cantidad debe ser mayor a cero")
        private BigDecimal cantidadOrdenada;

        @NotNull
        @DecimalMin(value = "0.0001", message = "El precio unitario debe ser mayor a cero")
        private BigDecimal precioUnitario;

        @NotNull
        @DecimalMin(value = "0.0001", message = "La cantidad debe ser mayor a cero")
        private BigDecimal cantidadRecibida;

        private String lote;
        private LocalDate fechaProduccion;
        private LocalDate fechaVencimiento;
    }
}
