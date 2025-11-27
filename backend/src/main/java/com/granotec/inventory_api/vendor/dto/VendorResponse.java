package com.granotec.inventory_api.vendor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VendorResponse {
    private Long id;
    private String razonSocial;   // <- antes 'nombre'
    private String tipoDocumento;
    private String nroDocumento;  // <- antes 'documento'
    private String direccion;
    private String telefono;
    private String email;
    private String notas;
    private String moneda;
    private String condicionPago;
}
