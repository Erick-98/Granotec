package com.granotec.inventory_api.customer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerResponse {
    private Long id;
    private String nombre;
    private String apellidos;
    private String razonSocial;
    private String zona;
    private String rubro;
    private String condicionPago;
    private BigDecimal limiteDolares;
    private BigDecimal limiteCreditoSoles;
    private String notas;
    private String tipoDocumento;
    private String nroDocumento;
    private String direccion;
    private String telefono;
    private String email;
    private String distrito;
    private String provincia;
    private String departamento;
    private String tipoCliente;
}

