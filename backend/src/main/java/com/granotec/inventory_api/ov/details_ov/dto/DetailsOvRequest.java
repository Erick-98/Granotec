package com.granotec.inventory_api.ov.details_ov.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetailsOvRequest {
    @NotNull
    private Integer idOv;
    @NotNull
    private Integer idProducto;
    @NotNull
    private BigDecimal cantidad;
    @NotNull
    private BigDecimal precioUnitario;
}

