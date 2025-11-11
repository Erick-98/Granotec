package com.granotec.inventory_api.product;


import com.granotec.inventory_api.common.enums.TipoPresentacion;
import com.granotec.inventory_api.common.enums.Tipo_Existencia;
import com.granotec.inventory_api.common.enums.TypeProduct;
import com.granotec.inventory_api.common.enums.UnitOfMeasure;
import com.granotec.inventory_api.common.model.BaseEntity;
import com.granotec.inventory_api.vendor.Vendor;
import com.granotec.inventory_api.common.enums.ProducciónStatus;
import com.granotec.inventory_api.product.familiaProducto.familiaProducto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "producto", indexes = {@Index(name = "idx_producto_codigo", columnList = "codigo")})
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 50, name = "codigo", unique = true)
    private String code;

    @Column(nullable = false, unique = true, length = 100)
    private String nombreComercial;

    @Transient
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_producto", nullable = false)
    private TypeProduct tipoProducto;

    @Column(name = "descripcion")
    private String description;

    @ManyToOne
    @JoinColumn(name = "proveedor_id")
    private Vendor proveedor;

    @Transient
    @Enumerated(EnumType.STRING)
    private Tipo_Existencia tipoExistencia;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_presentacion")
    private TipoPresentacion tipoPresentacion;

    @Enumerated(EnumType.STRING)
    @Column(name = "unidad_medida", length = 20)
    private UnitOfMeasure unitOfMeasure;

    @ManyToOne
    @JoinColumn(name = "familia_id")
    private familiaProducto familia;

    // Bloqueo por calidad o estado que impide movimientos hasta ser liberado
    @Column(name = "is_locked")
    private Boolean isLocked = Boolean.FALSE;

    @Column(name = "lock_reason")
    private String lockReason;

    @Enumerated(EnumType.STRING)
    private ProducciónStatus productionStatus = ProducciónStatus.READY;

}