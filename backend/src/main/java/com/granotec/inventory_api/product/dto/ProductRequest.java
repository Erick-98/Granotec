package com.granotec.inventory_api.product.dto;

import com.granotec.inventory_api.common.enums.TypeProduct;
import com.granotec.inventory_api.common.enums.UnitOfMeasure;
import com.granotec.inventory_api.common.enums.TipoPresentacion;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {
    @NotBlank
    private String code;
    @NotBlank
    private String nombreComercial;
    private String description;
    private Long proveedorId;
    private TipoPresentacion tipoPresentacion;
    private TypeProduct tipoProducto;
    private UnitOfMeasure unitOfMeasure;
    private Long familiaId;
    private Boolean blocked;
}
