package com.granotec.inventory_api.inventario.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;

@Data
public class TransferenciaRequest {
    @NotNull
    private Long almacenOrigenId;
    @NotNull
    private Long almacenDestinoId;
    @NotNull
    private Integer productoId;
    @NotNull
    @DecimalMin(value = "0.0001", message = "La cantidad debe ser mayor a cero")
    private BigDecimal cantidad;
    @NotNull
    private Long usuarioId;
    private String motivo;
    private List<LoteTransferenciaDTO> lotes; // Para transferencias por lote

    @Data
    public static class LoteTransferenciaDTO {
        @NotNull
        private Integer loteId;
        @NotNull
        @DecimalMin(value = "0.0001", message = "La cantidad debe ser mayor a cero")
        private BigDecimal cantidad;
    }
}
