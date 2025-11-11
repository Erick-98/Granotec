package com.granotec.inventory_api.Movimientos.dto;

import com.granotec.inventory_api.common.enums.TipoPresentacion;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoLineRequest {
    private String productCode;
    private String lote;
    private String ordenProduccion;
    private LocalDate fechaIngreso;
    private LocalDate fechaProduccion;
    private LocalDate fechaVencimiento;
    private TipoPresentacion presentacion;
    private BigDecimal cantidad;
    private BigDecimal costoUnitarioSoles;
    private BigDecimal costoUnitarioDolares;
}

