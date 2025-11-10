package com.granotec.inventory_api.Movimientos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoResponse {
    private Integer id;
    private LocalDate fechaDocumento;
    private Long almacenId;
    private String almacenNombre;
    private String nroFactura;
    private BigDecimal totalSoles;
    private BigDecimal totalDolares;
    private List<MovimientoLineResponse> detalles;
}

