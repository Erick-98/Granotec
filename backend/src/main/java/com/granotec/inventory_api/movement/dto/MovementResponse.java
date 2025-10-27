package com.granotec.inventory_api.movement.dto;

import com.granotec.inventory_api.movement.MovementKind;
import com.granotec.inventory_api.movement.OperationType;
import com.granotec.inventory_api.common.enums.Status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class MovementResponse {
    public Long id;
    public LocalDate fechaMovimiento;
    public Long almacenOrigenId;
    public Long almacenDestinoId;
    public MovementKind tipoMovimiento;
    public OperationType tipoOperacion;
    public String numeroFactura;
    public String observacion;
    public BigDecimal total;
    public Status estado;
    public List<MovementDetailResponse> detalles;
}

