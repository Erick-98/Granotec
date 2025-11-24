package com.granotec.inventory_api.ConsumoProduccion;

import com.granotec.inventory_api.Insumo.Insumo;
import com.granotec.inventory_api.OrdenProduccion.OrdenProduccion;
import com.granotec.inventory_api.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "consumo_produccion")
public class ConsumoProduccion extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "orden_produccion_id", nullable = false)
    private OrdenProduccion ordenProduccion;

    @ManyToOne
    @JoinColumn(name = "insumo_id", nullable = false)
    private Insumo insumo;

    private Integer loteInsumoId;


    @Column(nullable = false)
    private BigDecimal cantidadUsada;

    @Column(nullable = false)
    private BigDecimal costoUnitario;

    @Column(nullable = false)
    private BigDecimal costoTotal;

}
