package com.granotec.inventory_api.ov.dto;

import com.granotec.inventory_api.common.enums.Currency;
import com.granotec.inventory_api.common.enums.TipoOv;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OvRequest {
    @NotNull
    private String numeroDocumento;
    @NotNull
    private TipoOv tipoDocumento;
    @NotNull
    private Long idCliente;
    private Integer idVendedor;
    @NotNull
    private LocalDate fecha;
    private Currency moneda;
    @NotNull
    private BigDecimal total;
}

