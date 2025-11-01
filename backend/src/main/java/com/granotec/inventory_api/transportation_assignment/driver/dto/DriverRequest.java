package com.granotec.inventory_api.transportation_assignment.driver.dto;

import com.granotec.inventory_api.common.enums.DocumentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class DriverRequest {
    @NotBlank(message = "name es requerido")
    private String name;

    @NotBlank(message = "apellidos es requerido")
    private String apellidos;

    private DocumentType tipoDocumento;

    private String nroDocumento;

    @NotNull(message = "carrierId es requerido")
    private Integer carrierId;
}
