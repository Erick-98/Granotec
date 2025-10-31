package com.granotec.inventory_api.product.dto;

import com.granotec.inventory_api.product.UnitOfMeasure;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Integer id;
    private String code;
    private String name;
    private String description;
    private BigDecimal price;
    private UnitOfMeasure unitOfMeasure;
    private Boolean isLocked;
    private BigDecimal totalQuantity;
}

