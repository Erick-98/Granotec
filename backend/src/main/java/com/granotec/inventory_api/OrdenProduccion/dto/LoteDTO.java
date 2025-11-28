package com.granotec.inventory_api.OrdenProduccion.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class LoteDTO {
    private Integer id;
    private String codigoLote;
    private BigDecimal cantidadProducida;
    private BigDecimal costoUnitario;
    private BigDecimal costoTotal;
    private String estado;
    private Integer almacenId;
}
