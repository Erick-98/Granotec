package com.granotec.inventory_api.produccion.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class OrdenProduccionResponse {
    private Integer id;
    private Long productoId;
    private String productoNombre;
    private String fechaInicio;
    private String fechaFin;
    private String estado;
    private BigDecimal cantidadProducida;
    private List<ConsumoInsumoDTO> consumos;
    private Long almacenDestinoId;
    private String codigoLoteGenerado;

    @Data
    public static class ConsumoInsumoDTO {
        private Long insumoId;
        private String insumoNombre;
        private BigDecimal cantidad;
    }
}
