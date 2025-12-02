package com.granotec.inventory_api.inventario.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductoDisponibleResponse {
    private Long productoId;
    private String productoNombre;
    private String productoCodigo;
    private BigDecimal cantidadDisponible;
}

