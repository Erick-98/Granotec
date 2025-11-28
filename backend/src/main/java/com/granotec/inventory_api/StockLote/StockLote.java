package com.granotec.inventory_api.StockLote;

import com.granotec.inventory_api.Lote.Lote;
import com.granotec.inventory_api.common.model.BaseEntity;
import com.granotec.inventory_api.storage.Storage;
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
@Table(name = "stock_lote")
public class StockLote extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "lote_id", nullable = false)
    private Lote lote;

    @ManyToOne
    @JoinColumn(name = "almacen_id", nullable = false)
    private Storage almacen;

    @Column(nullable = false)
    private BigDecimal cantidadDisponible;

    private BigDecimal cantidadReservada = BigDecimal.ZERO;
}
