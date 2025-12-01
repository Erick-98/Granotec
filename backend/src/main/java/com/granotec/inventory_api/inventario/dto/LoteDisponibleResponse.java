package com.granotec.inventory_api.inventario.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class LoteDisponibleResponse {
    private Long loteId;
    private String codigoLote;
    private BigDecimal cantidadDisponible;
    private LocalDate fechaProduccion;
    private LocalDate fechaVencimiento;
    private BigDecimal costoUnitario;
}

