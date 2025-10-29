package com.granotec.inventory_api.salesperson.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalespersonRequest {
    @NotBlank
    private String name;
    private String apellidos;
    private String nroDocumento;
    private String telefono;
    private String email;
}

