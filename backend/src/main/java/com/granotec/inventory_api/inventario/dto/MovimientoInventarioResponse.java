package com.granotec.inventory_api.inventario.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class MovimientoInventarioResponse {
    private Long idMovimiento;
    private String tipoMovimiento;
    private String tipoOperacion;
    private Long almacenId;
    private Long productoId;
    private Long loteId;
    private BigDecimal cantidad;
    private String fechaMovimiento;
    private String observacion;
    private String usuario;
    private BigDecimal stockAnterior;
    private BigDecimal stockActual;
}

