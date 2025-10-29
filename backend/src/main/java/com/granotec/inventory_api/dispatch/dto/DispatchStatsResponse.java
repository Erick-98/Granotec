package com.granotec.inventory_api.dispatch.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DispatchStatsResponse {
    private long totalDispatches;
    private long pendingDispatches;
    private Double averageDeliveryDays;
}

