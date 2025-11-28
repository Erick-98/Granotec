package com.granotec.inventory_api.OrdenProduccion.dto;

import lombok.Data;

@Data
public class AprobarLaboratorioRequest {
    private Integer ordenId;
    private boolean aprobado;
    private String observacion;
}
