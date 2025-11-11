package com.granotec.inventory_api.product.costo;

import com.granotec.inventory_api.common.model.BaseEntity;
import com.granotec.inventory_api.product.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "costo")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Costo extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private Product producto;

    private String lote;

    private LocalDate fechaIngreso;

    private LocalDate fechaProduccion; // nullable

    @Column(precision = 19, scale = 4)
    private BigDecimal costoUnitarioSoles;

    @Column(precision = 19, scale = 4)
    private BigDecimal costoUnitarioDolares;

}
