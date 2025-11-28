package com.granotec.inventory_api.ventas.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class VentaResponse {
    private Integer id;
    private String numero;
    private Long clienteId;
    private String clienteNombre;
    private Long vendedorId;
    private String vendedorNombre;
    private Long almacenId;
    private String almacenNombre;
    private String fecha;
    private String estado;
    private BigDecimal total;
    private String observaciones;
    private List<DetalleVentaDTO> detalles;

    @Data
    public static class DetalleVentaDTO {
        private Long productoId;
        private String productoNombre;
        private Integer loteId;
        private String codigoLote;
        private BigDecimal cantidad;
        private BigDecimal precioUnitario;
        private BigDecimal subtotal;
    }
}

