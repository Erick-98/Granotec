package com.granotec.inventory_api.stock;

import com.granotec.inventory_api.common.model.BaseEntity;
import com.granotec.inventory_api.storage.entity.Storage;
import com.granotec.inventory_api.product.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "stock_adjustment")
public class StockAdjustment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_almacen")
    private Storage almacen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto")
    private Product producto;

    @Column(name = "lote", length = 100)
    private String lote;

    @Column(name = "delta", precision = 14, scale = 2, nullable = false)
    private BigDecimal delta;

    @Column(name = "notes", length = 500)
    private String notes;

    @Column(name = "created_by", length = 100)
    private String createdBy;
}
