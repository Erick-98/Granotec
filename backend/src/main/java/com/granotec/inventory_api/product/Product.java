package com.granotec.inventory_api.product;


import com.granotec.inventory_api.common.enums.UnitOfMeasure;
import com.granotec.inventory_api.common.model.BaseEntity;
import com.granotec.inventory_api.dispatch.details_dispatch.DetailsDispatch;
import com.granotec.inventory_api.ov.details_ov.Details_ov;
import com.granotec.inventory_api.stock.Stock;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "producto")
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 50, name = "codigo")
    private String code;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "descripcion")
    private String description;

    @Column(name = "precio", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    // Compatibilidad: campo transitorio stock para no romper tests/constructores existentes
    @Transient
    @Deprecated
    private Integer stock;

    // El campo 'stock' se normaliza en la tabla 'stock'. Mantener relación hacia los registros de stock por almacen/lote.
    @OneToMany(mappedBy = "producto", fetch = FetchType.LAZY)
    private List<Stock> stocks;

    @Column(name = "lote", length = 50)
    private String batch;

    @Enumerated(EnumType.STRING)
    @Column(name = "unidad_medida", length = 20)
    private UnitOfMeasure unitOfMeasure;

    // Bloqueo por calidad o estado que impide movimientos hasta ser liberado
    @Column(name = "is_locked")
    private Boolean isLocked = Boolean.FALSE;

    @Column(name = "lock_reason")
    private String lockReason;

    @OneToMany(mappedBy = "product")
    private List<Details_ov> detalles_ov;

    @OneToMany(mappedBy = "product")
    private List<DetailsDispatch> detalles_despacho;

    /*
    SUGERENCIA: para validar movimientos, comprobar product.isLocked antes de decrementar stock.
    */
}
