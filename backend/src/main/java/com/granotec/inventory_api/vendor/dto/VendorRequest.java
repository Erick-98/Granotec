package com.granotec.inventory_api.vendor.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VendorRequest {

    @NotBlank
    private String nombre;

    private String tipoDocumento;

    private String documento;

    private String direccion;

    private String telefono;

    @Email
    private String email;


}
