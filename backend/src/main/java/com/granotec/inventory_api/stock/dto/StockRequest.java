package com.granotec.inventory_api.stock.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockRequest {

    @NotNull(message = "idAlmacen es requerido")
    private Long idAlmacen;

    @NotNull(message = "idProducto es requerido")
    private Integer idProducto;

    private String lote;

    @NotNull(message = "cantidad es requerida")
    private BigDecimal cantidad;
}
