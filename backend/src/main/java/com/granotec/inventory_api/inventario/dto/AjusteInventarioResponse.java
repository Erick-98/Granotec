package com.granotec.inventory_api.inventario.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class AjusteInventarioResponse {
    private Long idAjuste;
    private String tipoAjuste;
    private Long almacenId;
    private String almacenNombre;
    private Long productoId;
    private String productoNombre;
    private Integer loteId;
    private String codigoLote;
    private BigDecimal cantidad;
    private String motivo;
    private String usuario;
    private String fechaAjuste;
}

