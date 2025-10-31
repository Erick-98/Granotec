package com.granotec.inventory_api.stock.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockResponse {
    private Long id;
    private Long idAlmacen;
    private Integer idProducto;
    private String lote;
    private BigDecimal cantidad;
}

