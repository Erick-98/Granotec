package com.granotec.inventory_api.Recojo.detalle;

import com.granotec.inventory_api.Recojo.Recojo;
import com.granotec.inventory_api.common.model.BaseEntity;
import com.granotec.inventory_api.product.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "recojo_detalle")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecojoDetalle extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "recojo_id")
    private Recojo recojo;

    @ManyToOne
    @JoinColumn(name = "producto_id")
    private Product producto;

    private Double kilos;
    private String lote;
}

