package com.granotec.inventory_api.stock.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockAdjustRequest {
    @NotNull
    private Long idAlmacen;
    @NotNull
    private Integer idProducto;
    private String lote;
    @NotNull
    private BigDecimal delta;
    private String notes;
}

