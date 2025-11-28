package com.granotec.inventory_api.OrdenProduccion.dto;

import com.granotec.inventory_api.common.enums.EstadoLaboratorio;
import com.granotec.inventory_api.common.enums.ProduccionStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class OrdenProduccionResponse {
    private Integer id;
    private String numero;
    private Integer productoId;
    private String productoNombre;
    private BigDecimal cantidadProgramada;
    private BigDecimal cantidadProducida;
    private ProduccionStatus estado;
    private EstadoLaboratorio estadoLaboratorio;
    private LocalDate fechaCreacion;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private BigDecimal costoEstimado;
    private BigDecimal costoReal;
    private List<ConsumoProduccionDTO> consumos;
    private List<LoteDTO> lotes;
}
