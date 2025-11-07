package com.granotec.inventory_api.customer.typeCustomer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TypeCustomerResponse {
    private Long id;
    private String nombre;
    private String descripcion;
}
