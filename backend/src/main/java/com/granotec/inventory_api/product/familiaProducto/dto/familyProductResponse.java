package com.granotec.inventory_api.product.familiaProducto.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class familyProductResponse {
    private Long id;
    private String nombre;
    private String descripcion;
}
