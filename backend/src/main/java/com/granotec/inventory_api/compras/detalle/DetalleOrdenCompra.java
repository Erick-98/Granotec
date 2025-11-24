package com.granotec.inventory_api.compras.detalle;

import com.granotec.inventory_api.OrdenCompra.OrdenCompra;
import com.granotec.inventory_api.product.Product;
import com.granotec.inventory_api.Lote.Lote;
import com.granotec.inventory_api.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "detalle_orden_compra")
public class DetalleOrdenCompra extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "orden_compra_id", nullable = false)
    private OrdenCompra ordenCompra;

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

