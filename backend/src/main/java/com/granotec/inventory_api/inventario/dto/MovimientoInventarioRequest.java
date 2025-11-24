package com.granotec.inventory_api.inventario.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class MovimientoInventarioRequest {
    @NotNull
    private Long almacenId;
    @NotNull
    private Integer productoId;
    @NotNull
    private BigDecimal cantidad;
    @NotNull
    private String tipoMovimiento; // ENTRADA, SALIDA, AJUSTE, TRANSFERENCIA
    private String motivo; // Para ajustes
    private String observacion;
    private Long loteId; // Opcional para movimientos por lote
    private Long usuarioId;
}

