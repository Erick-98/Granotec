package com.granotec.inventory_api.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductPriceResponse {
    private Integer productoId;
    private String nombreProducto;
    private Long almacenId;
    private String nombreAlmacen;
    private BigDecimal precioPromedioPonderado;
    private BigDecimal stockDisponible;
    private String mensaje;
}

