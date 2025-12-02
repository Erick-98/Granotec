package com.granotec.inventory_api.inventario.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class StockLoteResponse {
    private Integer stockLoteId;
    private Integer loteId;
    private String codigoLote;
    private Long almacenId;
    private String almacenNombre;
    private Integer productoId;
    private String productoNombre;
    private BigDecimal cantidadDisponible;
    private String estadoLote;
    private String fechaProduccion;
    private BigDecimal costoUnitario;
    private BigDecimal precioVentaUnitario;
}

