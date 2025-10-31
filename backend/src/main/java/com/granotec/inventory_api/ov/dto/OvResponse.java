package com.granotec.inventory_api.ov.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OvResponse {
    private Integer id;
    private String numeroDocumento;
    private String tipoDocumento;
    private Long idCliente;
    private Integer idVendedor;
    private LocalDate fecha;
    private String moneda;
    private BigDecimal total;
}

