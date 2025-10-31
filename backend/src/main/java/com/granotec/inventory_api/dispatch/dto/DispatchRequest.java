package com.granotec.inventory_api.dispatch.dto;

import com.granotec.inventory_api.common.enums.Status;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DispatchRequest {
    @NotNull
    private Integer idOrdenVenta;

    @NotNull
    private Integer idAsignacion;

    @NotNull
    private LocalDate fechaDespacho;

    @NotNull
    private Status estado;
}

