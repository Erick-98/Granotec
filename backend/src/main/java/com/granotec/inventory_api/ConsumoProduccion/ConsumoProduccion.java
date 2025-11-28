package com.granotec.inventory_api.ConsumoProduccion;

import com.granotec.inventory_api.OrdenProduccion.OrdenProduccion;
import com.granotec.inventory_api.StockLote.StockLote;
import com.granotec.inventory_api.common.enums.EstadoConsumo;
import com.granotec.inventory_api.common.model.BaseEntity;
import com.granotec.inventory_api.product.Product;
import com.granotec.inventory_api.storage.Storage;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

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
    private Product insumo;

    @ManyToOne
    @JoinColumn(name = "stock_lote_origen_id")
    private StockLote stockLoteOrigen;

    @ManyToOne
    @JoinColumn(name = "almacen_origen_id")
    private Storage almacenOrigen;

    @Column(nullable = false)
    private BigDecimal cantidadUsada;

    private LocalDateTime fechaConsumo;

    @Column(nullable = false)
    private BigDecimal costoUnitario;

    @Column(nullable = false)
    private BigDecimal costoTotal;

    @Enumerated(EnumType.STRING)
    private EstadoConsumo estadoConsumo;

}
