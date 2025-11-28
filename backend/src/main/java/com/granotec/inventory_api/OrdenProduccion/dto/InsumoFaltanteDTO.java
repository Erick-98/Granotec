package com.granotec.inventory_api.OrdenProduccion.dto;

import java.math.BigDecimal;
import java.util.List;

public record InsumoFaltanteDTO(
        Integer insumoId,
        String insumoNombre,
        BigDecimal cantidadRequerida,
        BigDecimal cantidadDisponible,
        BigDecimal cantidadFaltante,
        List<StockDetalleDTO> detallePorAlmacen
) {
}
