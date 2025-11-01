package com.granotec.inventory_api.dispatch.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DispatchResponse {
    private Integer id;
    private Integer idOrdenVenta;
    private Integer idAsignacion;
    private LocalDate fechaDespacho;
    private String estado;
}

