package com.granotec.inventory_api.OrdenProduccion.dto;

import com.granotec.inventory_api.common.enums.EstadoConsumo;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ConsumoProduccionDTO{
    private Integer id;
    private Integer insumoId;
    private String insumoNombre;
    private Integer stockLoteOrigenId;
    private Long almacenOrigenId;
    private BigDecimal cantidad;
    private BigDecimal costoUnitario;
    private BigDecimal costoTotal;
    private EstadoConsumo estado;
    private LocalDateTime fecha;

}
