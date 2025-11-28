package com.granotec.inventory_api.OrdenProduccion.ListaMaterial.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ListaMaterialResponse {
    private Integer id;
    private Integer productoId;
    private String productoNombre;
    private String version;
    private List<ItemListaResponse> items;

    @Data
    public static class ItemListaResponse{
        private Integer id;
        private Integer insumoId;
        private String insumoNombre;
        private BigDecimal cantidadPorUnidad;
    }

}
