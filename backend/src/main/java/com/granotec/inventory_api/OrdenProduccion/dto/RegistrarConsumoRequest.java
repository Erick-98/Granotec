package com.granotec.inventory_api.OrdenProduccion.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class RegistrarConsumoRequest {
    private Integer ordenId;
    private Integer insumoId;
    private Integer stockLoteOrigenId; // opcional: si no se pasa, se selecciona FIFO
    private BigDecimal cantidad;
    private LocalDateTime fechaConsumo;
}
