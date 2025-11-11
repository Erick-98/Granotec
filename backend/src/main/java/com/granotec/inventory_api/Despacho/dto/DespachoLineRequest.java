package com.granotec.inventory_api.Despacho.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DespachoLineRequest {
    private String productCode;
    private String lote;
    private Double kilos;
}

