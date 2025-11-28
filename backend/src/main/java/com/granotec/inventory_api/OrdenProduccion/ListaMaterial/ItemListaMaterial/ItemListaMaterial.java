package com.granotec.inventory_api.OrdenProduccion.ListaMaterial.ItemListaMaterial;

import com.granotec.inventory_api.OrdenProduccion.ListaMaterial.ListaMaterial;
import com.granotec.inventory_api.common.model.BaseEntity;
import com.granotec.inventory_api.product.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "item_lista_material")
public class ItemListaMaterial extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "lista_material_id", nullable = false)
    private ListaMaterial listaMaterial;

    @ManyToOne
    @JoinColumn(name = "insumo_id", nullable = false)
    private Product insumo;

    @Column(nullable = false,precision = 19, scale = 6)
    private BigDecimal cantidadPorUnidad;
}
