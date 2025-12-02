package com.granotec.inventory_api.product.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductStockDetailsResponse {
    private Integer productId;
    private BigDecimal totalQuantity;
    private List<StockLine> lines;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StockLine {
        public Long stockId;
        public Long almacenId;
        public String lote;
        public BigDecimal cantidad;
    }
}

