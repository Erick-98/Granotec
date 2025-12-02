package com.granotec.inventory_api.salesperson.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalespersonRequest {
    @NotBlank
    private String name;

    @NotBlank
    private String apellidos;

    @Size(max = 8)
    @NotBlank(message = "El documento es obligatorio.")
    private String nroDocumento;

    @NotNull(message = "Debe seleccionar un distrito")
    private Integer distritoId;

    private String direccion;

    @Size(max = 9, message = "El teléfono no debe exceder los 9 caracteres.")
    private String telefono;

    @Email(message = "El email debe tener un formato válido.")
    private String email;
}

