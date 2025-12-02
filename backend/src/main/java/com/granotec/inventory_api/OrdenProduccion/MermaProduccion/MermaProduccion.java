package com.granotec.inventory_api.OrdenProduccion.MermaProduccion;

import com.granotec.inventory_api.OrdenProduccion.OrdenProduccion;
import com.granotec.inventory_api.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "merma_produccion")
public class MermaProduccion extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "orden_produccion_id", nullable = false)
    private OrdenProduccion ordenProduccion;

    private BigDecimal cantidadProgramada;
    private BigDecimal cantidadProducida;
    private BigDecimal cantidadMerma;
    private BigDecimal costoMerma;
    private LocalDate fechaRegistro;
}
