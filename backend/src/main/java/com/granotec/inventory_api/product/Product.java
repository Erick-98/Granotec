package com.granotec.inventory_api.product;


import com.granotec.inventory_api.common.model.BaseEntity;
import com.granotec.inventory_api.dispatch.details_dispatch.DetailsDispatch;
import com.granotec.inventory_api.ov.details_ov.Details_ov;
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

    @Column(name = "stock", nullable = false)
    private Integer stock;

    @Column(name = "lote", length = 50, nullable = false)
    private String batch;

    @OneToMany(mappedBy = "product")
    private List<Details_ov> detalles_ov;

    @OneToMany(mappedBy = "product")
    private List<DetailsDispatch> detalles_despacho;

    /*
    TENER EN CUENTA EL BLOQUEO DE PRODUCTOS POR CALIDAD, SI ES QUE PUEDE AÑADIR DESDE ACÁ UN LOCK
    puede ser un booleano "isLocked" o un enum "status" con valores como "AVAILABLE", "LOCKED", "UNDER_REVIEW", etc.
    FALTA AÑADIR UNIDAD DE MEDIDA y/o otros campos necesarios que aún no se me ocurreen jaja
    * */
}
