package com.granotec.inventory_api.inventario.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.Valid;

@Data
public class TransferenciaRequest {
    @NotNull(message = "El almacén origen es requerido")
    private Long almacenOrigenId;

    @NotNull(message = "El almacén destino es requerido")
    private Long almacenDestinoId;

    @NotNull(message = "El producto es requerido")
    private Integer productoId;

    @NotNull(message = "El usuario es requerido")
    private Long usuarioId;

    private String motivo;

    // Lotes específicos a transferir (si se especifica, se ignora 'cantidad')
    @Valid
    private List<LoteTransferenciaDTO> lotes;

    // Cantidad total (usado solo si 'lotes' es null - transferencia FIFO automática)
    @DecimalMin(value = "0.0001", message = "La cantidad debe ser mayor a cero")
    private BigDecimal cantidad;

    @Data
    public static class LoteTransferenciaDTO {
        @NotNull(message = "El ID del lote es requerido")
        private Long loteId;

        @NotNull(message = "La cantidad es requerida")
        @DecimalMin(value = "0.0001", message = "La cantidad debe ser mayor a cero")
        private BigDecimal cantidad;
    }
}
