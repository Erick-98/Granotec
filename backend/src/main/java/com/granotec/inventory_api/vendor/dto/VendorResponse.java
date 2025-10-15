package com.granotec.inventory_api.vendor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VendorResponse {
    private Long id;
    private String nombre;
    private String tipoDocumento;
    private String documento;
    private String direccion;
    private String telefono;
    private String email;
}
