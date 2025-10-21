package com.granotec.inventory_api.storage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StorageResponse {

    private Long id;
    private String nombre;
    private String descripcion;

}
