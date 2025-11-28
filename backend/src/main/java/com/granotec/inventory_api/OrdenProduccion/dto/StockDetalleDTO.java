package com.granotec.inventory_api.OrdenProduccion.dto;

import java.math.BigDecimal;

public record StockDetalleDTO (
     Long almacenId,
     String almacenNombre,
     Integer loteId,
     String codigoLote,
     BigDecimal cantidadDisponible
) {}
