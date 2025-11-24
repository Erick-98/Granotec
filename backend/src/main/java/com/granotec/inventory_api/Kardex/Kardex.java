package com.granotec.inventory_api.Kardex;

import com.granotec.inventory_api.Lote.Lote;
import com.granotec.inventory_api.common.enums.TipoMovimiento;
import com.granotec.inventory_api.common.enums.TypeOperation;
import com.granotec.inventory_api.common.model.BaseEntity;
import com.granotec.inventory_api.product.Product;
import com.granotec.inventory_api.storage.Storage;
import com.granotec.inventory_api.user.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "kardex")
@Data
@NoArgsConstructor
public class Kardex extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate fechaMovimiento;

    @ManyToOne
    @JoinColumn(name = "almacen_id", nullable = false)
    private Storage almacen;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoMovimiento tipoMovimiento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeOperation tipoOperacion;

    //# Factura/Guía
    private String referencia;

    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private Product producto;

    @ManyToOne
    @JoinColumn(name = "lote_id")
    private Lote lote;

    private String OP;

    @Column(nullable = false)
    private BigDecimal cantidad;

    //PARA PRUEBAS
    private BigDecimal stockAnterior;
    private BigDecimal stockActual;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private User usuario;


    private String observacion;

    @PrePersist
    protected void onCreate() {
        if (fechaMovimiento == null) {
            fechaMovimiento = LocalDate.now();
        }
    }
}
