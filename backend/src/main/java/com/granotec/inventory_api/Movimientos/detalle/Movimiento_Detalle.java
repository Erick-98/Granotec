package com.granotec.inventory_api.Movimientos.detalle;

import com.granotec.inventory_api.Movimientos.Movimiento;
import com.granotec.inventory_api.common.enums.TipoPresentacion;
import com.granotec.inventory_api.common.enums.TypeProduct;
import com.granotec.inventory_api.common.model.BaseEntity;
import com.granotec.inventory_api.product.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "movimiento_detalle")
public class Movimiento_Detalle extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "movimiento_id")
    private Movimiento movimiento;

    @ManyToOne
    @JoinColumn(name = "producto_id")
    private Product producto;

    private String lote;
    private String ordenProduccion;
    private LocalDate fechaIngreso;
    private LocalDate fechaProduccion;
    private LocalDate fechaVencimiento;

    @Enumerated(EnumType.STRING)
    private TipoPresentacion presentacion;

    private BigDecimal cantidad;

    @Column(precision = 19, scale = 4)
    private BigDecimal costoUnitarioSoles;

    @Column(precision = 19, scale = 4)
    private BigDecimal costoUnitarioDolares;

    private String familiaProducto;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_producto", nullable = false)
    private TypeProduct tipoProducto;


}
