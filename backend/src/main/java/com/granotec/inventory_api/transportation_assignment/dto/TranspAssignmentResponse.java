package com.granotec.inventory_api.transportation_assignment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TranspAssignmentResponse {
    private Integer id;
    private Integer carrierId;
    private Long driverId;
    private Long carId;
}

