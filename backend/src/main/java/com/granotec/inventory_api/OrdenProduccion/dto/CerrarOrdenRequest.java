package com.granotec.inventory_api.OrdenProduccion.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CerrarOrdenRequest {
    private Integer ordenId;
    private BigDecimal cantidadProducida;
    private String codigoLote;
    private BigDecimal precioVentaUnitario;
    private BigDecimal costoUnitarioFinal;
    private Long almacenDestinoId;
}
