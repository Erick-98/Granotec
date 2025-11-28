package com.granotec.inventory_api.produccion.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class OrdenProduccionRequest {
    @NotNull
    private Integer productoId;
    @NotNull
    @DecimalMin(value = "0.0001", message = "La cantidad a producir debe ser mayor a cero")
    private BigDecimal cantidadProducir;
    @NotNull
    private String fechaInicio;
    private String fechaFin;
    @NotNull
    private Long almacenDestinoId;
    private String loteCodigoManual;
    private List<ConsumoInsumoDTO> consumos;

    @Data
    public static class ConsumoInsumoDTO {
        private Long insumoId;
        @DecimalMin(value = "0.0001", message = "La cantidad debe ser mayor a cero")
        private BigDecimal cantidad;
    }
}
