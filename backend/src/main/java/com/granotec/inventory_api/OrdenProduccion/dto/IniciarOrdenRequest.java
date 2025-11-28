package com.granotec.inventory_api.OrdenProduccion.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class IniciarOrdenRequest {
    private Integer ordenId;
    private LocalDate fechaInicio;
    private List<Long> almacenesPrioritarios;
}
