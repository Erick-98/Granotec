package com.granotec.inventory_api.dispatch.details_dispatch.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetailsDispatchResponse {
    private Integer id;
    private Integer idDespacho;
    private Integer idProducto;
    private BigDecimal cantidad;
}

