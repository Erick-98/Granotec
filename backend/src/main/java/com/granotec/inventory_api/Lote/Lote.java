package com.granotec.inventory_api.Lote;

import com.granotec.inventory_api.OrdenProduccion.OrdenProduccion;
import com.granotec.inventory_api.StockLote.StockLote;
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
@Table(name = "lote")
public class Lote extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "orden_produccion_id")
    private OrdenProduccion ordenProduccion;

    @ManyToOne(optional = false)
    @JoinColumn(name = "producto_id")
    private Product producto;

    @Column(nullable = false, unique = true, length = 50)
    private String codigoLote;

    private LocalDate fechaProduccion;

    private LocalDate fechaVencimiento;

    @Column(nullable = false)
    private BigDecimal cantidadProducida;

    @Column(nullable = false)
    private BigDecimal costoTotal;

    @Column(nullable = false)
    private BigDecimal costoUnitario;

    @Column(nullable = false)
    private BigDecimal precioVentaUnitario;

    @OneToOne(mappedBy = "lote", cascade = CascadeType.ALL)
    private StockLote stock;

    @Column(nullable = false, length = 30)
    private String estado;


}
