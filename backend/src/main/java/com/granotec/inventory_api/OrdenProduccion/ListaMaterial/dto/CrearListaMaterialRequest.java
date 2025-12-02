package com.granotec.inventory_api.OrdenProduccion.ListaMaterial.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CrearListaMaterialRequest {
    private Integer productoId;
    private String version;
    private List<ItemMaterialRequest> items;

    @Data
    public static class ItemMaterialRequest{
        private Integer insumoId;
        private BigDecimal cantidadPorUnidad;
    }
}
