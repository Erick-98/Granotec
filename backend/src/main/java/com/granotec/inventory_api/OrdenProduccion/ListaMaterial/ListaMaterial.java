package com.granotec.inventory_api.OrdenProduccion.ListaMaterial;

import com.granotec.inventory_api.OrdenProduccion.ListaMaterial.ItemListaMaterial.ItemListaMaterial;
import com.granotec.inventory_api.common.model.BaseEntity;
import com.granotec.inventory_api.product.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "lista_material")
public class ListaMaterial extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private Product producto;

    private String version;

    @OneToMany(mappedBy = "listaMaterial", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ItemListaMaterial> items = new ArrayList<>();
}
