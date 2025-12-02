package com.granotec.inventory_api.OrdenProduccion.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CrearOrdenProduccionRequest {
    private String numero;
    private Integer productoId;
    private BigDecimal cantidadProgramada;
    private Long almacenDestinoId;
    private Integer listaMaterialId;
}
