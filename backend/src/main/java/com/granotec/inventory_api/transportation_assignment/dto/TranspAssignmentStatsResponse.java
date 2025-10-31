package com.granotec.inventory_api.transportation_assignment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TranspAssignmentStatsResponse {
    private long totalAssignments;
    private long totalDispatches;
    private long pendingDispatches;
    private Double avgDeliveryDays;
}

