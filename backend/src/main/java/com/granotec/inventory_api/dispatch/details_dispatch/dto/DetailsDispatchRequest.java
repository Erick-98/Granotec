package com.granotec.inventory_api.dispatch.details_dispatch.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetailsDispatchRequest {
    @NotNull
    private Integer idDespacho;

    @NotNull
    private Integer idProducto;

    @NotNull
    private BigDecimal cantidad;
}

