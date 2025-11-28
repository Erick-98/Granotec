package com.granotec.inventory_api.OrdenCompra;

import com.granotec.inventory_api.OrdenCompra.detalle.DetalleOrdenCompra;
import com.granotec.inventory_api.vendor.Vendor;
import com.granotec.inventory_api.storage.Storage;
import com.granotec.inventory_api.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "orden_compra")
public class OrdenCompra extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String numero;

    @ManyToOne
    @JoinColumn(name = "proveedor_id", nullable = false)
    private Vendor proveedor;

    @ManyToOne
    @JoinColumn(name = "almacen_id", nullable = false)
    private Storage almacen;

    private LocalDate fecha;

    @Column(nullable = false)
    private BigDecimal total;

    @OneToMany(mappedBy = "ordenCompra", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DetalleOrdenCompra> detalles = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (fecha == null) fecha = LocalDate.now();
    }
}
