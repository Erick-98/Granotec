package com.granotec.inventory_api.Despacho.detalles;

import com.granotec.inventory_api.Despacho.Despacho;
import com.granotec.inventory_api.common.model.BaseEntity;
import com.granotec.inventory_api.product.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "despacho_detalles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Despacho_detalles extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "despacho_id")
    private Despacho despacho;

    @ManyToOne
    @JoinColumn(name = "producto_id")
    private Product producto;

    private String lote;
    private Double kilos;

    @Column(precision = 19, scale = 4)
    private BigDecimal totalDolaresSinIgv;
}
