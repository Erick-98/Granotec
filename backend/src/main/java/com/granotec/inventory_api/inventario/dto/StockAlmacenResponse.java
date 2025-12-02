package com.granotec.inventory_api.inventario.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class StockAlmacenResponse {
    private Long stockAlmacenId;
    private Long almacenId;
    private String almacenNombre;
    private Long productoId;
    private String productoNombre;
    private BigDecimal cantidad;
}

