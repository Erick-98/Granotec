package com.granotec.inventory_api.transportation_assignment.carrier.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CarrierResponse {
    private Integer id;
    private String razonSocial;
    private String tipoDocumento;
    private String documentNumber;
    private String email;
    private String phone;
}

