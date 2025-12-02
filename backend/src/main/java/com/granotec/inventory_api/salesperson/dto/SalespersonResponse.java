package com.granotec.inventory_api.salesperson.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalespersonResponse {
    private Integer id;
    private String name;
    private String apellidos;
    private String nroDocumento;
    private String direccion;
    private String distrito;
    private String provincia;
    private String departamento;
    private String telefono;
    private String email;
}

