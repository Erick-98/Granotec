package com.granotec.inventory_api.product.dto;

import com.granotec.inventory_api.common.enums.TipoPresentacion;
import com.granotec.inventory_api.common.enums.TypeProduct;
import com.granotec.inventory_api.common.enums.UnitOfMeasure;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Integer id;
    private String code;
    private String name;
    private String description;
    private UnitOfMeasure unitOfMeasure;
    private TipoPresentacion tipoPresentacion;
    private TypeProduct tipoProducto;
    private String proveedor;
    private String familia;
    private Boolean isLocked;
}
