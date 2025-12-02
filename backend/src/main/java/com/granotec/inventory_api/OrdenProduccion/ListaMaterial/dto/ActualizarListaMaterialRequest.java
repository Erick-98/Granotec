package com.granotec.inventory_api.OrdenProduccion.ListaMaterial.dto;

import lombok.Data;

import java.util.List;

@Data
public class ActualizarListaMaterialRequest {
    private String version;
    private List<CrearListaMaterialRequest.ItemMaterialRequest> items;
}
