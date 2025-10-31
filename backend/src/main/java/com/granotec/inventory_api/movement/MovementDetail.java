package com.granotec.inventory_api.movement;

import com.granotec.inventory_api.common.model.BaseEntity;
import com.granotec.inventory_api.product.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "movimiento_detalle")
public class MovementDetail extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_movimiento", nullable = false)
    private Movement movement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto", nullable = false)
    private Product product;

    @Column(name = "nombre_comercial", length = 200)
    private String nombreComercial;

    @Column(name = "codigo", length = 100)
    private String codigo;

    @Column(name = "lote", length = 100)
    private String lote;

    @Column(name = "orden_produccion", length = 100)
    private String ordenProduccion;

    @Column(name = "fecha_ingreso")
    private LocalDate fechaIngreso;

    @Column(name = "fecha_produccion")
    private LocalDate fechaProduccion;

    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimiento;

    @Column(name = "presentacion", length = 200)
    private String presentacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_proveedor")
    private com.granotec.inventory_api.vendor.Vendor proveedor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente")
    private com.granotec.inventory_api.customer.Customer clienteDestino;

    @Column(name = "cantidad", precision = 14, scale = 2)
    private BigDecimal cantidad;

    @Column(name = "total", precision = 14, scale = 2)
    private BigDecimal total;
}
