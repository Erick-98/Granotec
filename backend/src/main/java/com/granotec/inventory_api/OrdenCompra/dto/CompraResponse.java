package com.granotec.inventory_api.OrdenCompra.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class CompraResponse {

    private Integer id;
    private String numero;
    private Long proveedorId;
    private String proveedorNombre;
    private Long almacenId;
    private String almacenNombre;
    private String fecha;
    private String estado;
    private BigDecimal total;
    private List<DetalleCompraDTO> detalles;

    @Data
    public static class DetalleCompraDTO {
        private Long productoId;
        private String productoNombre;
        private Integer loteId;
        private String codigoLote;
        private BigDecimal cantidad;
        private BigDecimal precioUnitario;
        private BigDecimal subtotal;
        private String estado;
        private String fechaProduccion;
        private String fechaVencimiento;
    }
}

