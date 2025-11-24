package com.granotec.inventory_api.inventario.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class AjusteInventarioRequest {
    private Long almacenId;
    private Integer productoId;
    private Integer loteId; // Opcional
    private BigDecimal cantidad;
    private String tipoAjuste; // POSITIVO o NEGATIVO
    private String motivo;
    private Long usuarioId;
}

