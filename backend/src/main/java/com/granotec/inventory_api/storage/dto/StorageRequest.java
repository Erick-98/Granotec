package com.granotec.inventory_api.storage.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StorageRequest {

    @NotNull(message = "nombre is required")
    private String nombre;

    @NotBlank(message = "descripcion is required")
    private String descripcion;
}
