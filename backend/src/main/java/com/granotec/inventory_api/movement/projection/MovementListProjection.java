package com.granotec.inventory_api.movement.projection;

import com.granotec.inventory_api.movement.MovementKind;
import com.granotec.inventory_api.movement.OperationType;
import com.granotec.inventory_api.common.enums.Status;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface MovementListProjection {
    Long getId();
    LocalDate getFechaMovimiento();
    String getNumeroFactura();
    MovementKind getTipoMovimiento();
    OperationType getTipoOperacion();
    Status getEstado();
    Long getAlmacenOrigenId();
    Long getAlmacenDestinoId();
    Integer getProductId();
    String getProductName();
    String getLote();
    BigDecimal getCantidad();
    BigDecimal getTotal();
}

