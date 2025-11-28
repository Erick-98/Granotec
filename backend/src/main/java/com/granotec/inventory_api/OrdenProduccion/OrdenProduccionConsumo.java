package com.granotec.inventory_api.OrdenProduccion;

import com.granotec.inventory_api.common.model.BaseEntity;
import com.granotec.inventory_api.product.Product;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "orden_produccion_consumo")
public class OrdenProduccionConsumo extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "orden_produccion_id")
    private OrdenProduccion ordenProduccion;

    @ManyToOne(optional = false)
    @JoinColumn(name = "producto_insumo_id")
    private Product productoInsumo;

    @Column(nullable = false, precision = 19, scale = 6)
    private BigDecimal cantidadConsumida;

    // Referencia opcional al lote origen principal si se desea trazabilidad agregada (puede haber múltiples lotes)
    @Column(name = "lote_origen_codigo", length = 60)
    private String loteOrigenCodigo;
}

