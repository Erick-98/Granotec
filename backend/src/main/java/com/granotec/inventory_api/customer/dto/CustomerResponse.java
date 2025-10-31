package com.granotec.inventory_api.customer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponse {
    private Long id;
    private String nombre;
    private String apellidos;
    private String razonSocial;
    private String tipoDocumento;
    private String documento;
    private String direccion;
    private String telefono;
    private String email;
}

