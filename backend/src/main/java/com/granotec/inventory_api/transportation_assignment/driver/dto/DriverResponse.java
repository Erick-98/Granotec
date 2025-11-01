package com.granotec.inventory_api.transportation_assignment.driver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DriverResponse {
    private Long id;
    private String name;
    private String apellidos;
    private Integer carrierId;
}

