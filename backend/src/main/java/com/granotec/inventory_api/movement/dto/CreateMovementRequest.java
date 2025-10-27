package com.granotec.inventory_api.movement.dto;

import com.granotec.inventory_api.movement.MovementKind;
import com.granotec.inventory_api.movement.OperationType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class CreateMovementRequest {
    public LocalDate fechaMovimiento;
    public Long almacenOrigenId;
    public Long almacenDestinoId;
    public MovementKind tipoMovimiento;
    public OperationType tipoOperacion;
    public String numeroFactura;
    public String observacion;
    public BigDecimal total;
    public List<CreateMovementDetail> detalles;

    public static class CreateMovementDetail {
        public Integer productId;
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
}

