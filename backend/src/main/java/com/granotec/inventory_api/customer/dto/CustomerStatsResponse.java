package com.granotec.inventory_api.customer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerStatsResponse {
    private Long customerId;
    private long totalOrders;
    private BigDecimal totalAmount;
    private LocalDate lastOrderDate;
}

