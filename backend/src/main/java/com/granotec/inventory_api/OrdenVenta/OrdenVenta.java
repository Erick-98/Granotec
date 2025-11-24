package com.granotec.inventory_api.OrdenVenta;

import com.granotec.inventory_api.OrdenVenta.detalles.DetalleOrdenVenta;
import com.granotec.inventory_api.common.enums.EstadoOrden_Venta;
import com.granotec.inventory_api.common.model.BaseEntity;
import com.granotec.inventory_api.customer.Customer;
import com.granotec.inventory_api.salesperson.Salesperson;
import com.granotec.inventory_api.storage.Storage;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "orden_venta")
public class OrdenVenta extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String numero;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Customer cliente;

    @ManyToOne
    @JoinColumn(name = "vendedor_id", nullable = false)
    private Salesperson vendedor;

    @ManyToOne
    @JoinColumn(name = "almacen_id", nullable = false)
    private Storage almacen;

    private LocalDate fecha;

    @Enumerated(EnumType.STRING)
    private EstadoOrden_Venta estado;

    @Column(nullable = false)
    private BigDecimal total;

    private String observaciones;

    private LocalDateTime fechaCreacion;

    @OneToMany(mappedBy = "ordenVenta", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DetalleOrdenVenta> detalles = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        if (estado == null) {
            estado = EstadoOrden_Venta.PENDIENTE;
        }
    }

}
