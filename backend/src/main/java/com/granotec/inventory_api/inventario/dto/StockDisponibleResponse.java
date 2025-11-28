package com.granotec.inventory_api.inventario.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class StockDisponibleResponse {
    private Long productoId;
    private String productoNombre;
    private Long almacenId;
    private String almacenNombre;
    private BigDecimal cantidadDisponible;
    private Long loteId;
    private String loteCodigo;

}
