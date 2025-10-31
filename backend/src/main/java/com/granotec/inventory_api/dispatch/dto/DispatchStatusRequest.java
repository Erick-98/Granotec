package com.granotec.inventory_api.dispatch.dto;

import com.granotec.inventory_api.common.enums.Status;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DispatchStatusRequest {
    @NotNull
    private Status estado;
}

