package com.granotec.inventory_api.Recojo.detalles;

import com.granotec.inventory_api.Recojo.Recojo;
import com.granotec.inventory_api.common.model.BaseEntity;
import com.granotec.inventory_api.product.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "recojo")
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

    private BigDecimal kilos;
    private String choferAsignado;
    private String placa;


    @Column(length = 1000)
    private String observaciones;
}
