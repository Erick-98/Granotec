package com.granotec.inventory_api.movement.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class MovementDetailResponse {
    public Long id;
    public Long movementId;
    public Integer productId; // Cambiado a Integer para coincidir con Product.id
    public String nombreComercial;
    public String codigo;
    public String lote;
    public String ordenProduccion;
    public LocalDate fechaIngreso;
    public LocalDate fechaProduccion;
    public LocalDate fechaVencimiento;
    public String presentacion;
    public Long proveedorId;
    public Long clienteDestinoId;
    public BigDecimal cantidad;
    public BigDecimal total;
}
