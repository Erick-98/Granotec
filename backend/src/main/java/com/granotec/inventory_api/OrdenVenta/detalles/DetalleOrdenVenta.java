package com.granotec.inventory_api.OrdenVenta.detalles;

import com.granotec.inventory_api.Lote.Lote;
import com.granotec.inventory_api.OrdenVenta.OrdenVenta;
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
@Table(name = "detalles_orden_venta")
public class DetalleOrdenVenta extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "orden_venta_id", nullable = false)
    private OrdenVenta ordenVenta;

    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private Product producto;

    @ManyToOne
    @JoinColumn(name = "lote_id")
    private Lote lote;

    @Column(nullable = false)
    private BigDecimal cantidad;

    @Column(nullable = false)
    private BigDecimal precioUnitario;

    private BigDecimal subtotal;

    @PrePersist
    @PreUpdate
    protected void calcularSubtotal() {
        this.subtotal = this.cantidad.multiply(this.precioUnitario);
    }
}
