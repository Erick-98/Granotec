package com.granotec.inventory_api.ov.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OvStatsResponse {
    private Long customerId;
    private long totalOrders;
    private BigDecimal totalAmount;
}

