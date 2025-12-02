package com.granotec.inventory_api.transportation_assignment.carrier.dto;

import com.granotec.inventory_api.common.enums.DocumentType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CarrierRequest {
    @NotBlank(message = "razonSocial es requerido")
    private String razonSocial;

    @NotNull(message = "tipoDocumento es requerido")
    private DocumentType tipoDocumento;

    private String documentNumber;

    @Size(max = 100)
    @Email
    private String email;

    @Size(max = 9)
    private String phone;
}
