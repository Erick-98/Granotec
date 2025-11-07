package com.granotec.inventory_api.movement;

import com.granotec.inventory_api.common.model.BaseEntity;
import com.granotec.inventory_api.common.enums.Status;
import com.granotec.inventory_api.storage.Storage;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "movimiento")
public class Movement extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fecha_movimiento", nullable = false)
    private LocalDate fechaMovimiento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_almacen_origen")
    private Storage almacenOrigen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_almacen_destino")
    private Storage almacenDestino;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_movimiento", nullable = false, length = 30)
    private MovementKind tipoMovimiento; // salida, entrada, saldo_inicial, transferencia

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_operacion", nullable = false, length = 30)
    private OperationType tipoOperacion; // compra, venta, etc.

    @Column(name = "numero_factura", length = 100)
    private String numeroFactura;

    @Column(name = "observacion", length = 500)
    private String observacion;

    @Column(name = "total", precision = 14, scale = 2)
    private java.math.BigDecimal total;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", length = 15)
    private Status estado;

    @OneToMany(mappedBy = "movement", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MovementDetail> detalles;
}
