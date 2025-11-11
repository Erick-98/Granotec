package com.granotec.inventory_api.Movimientos;


import com.granotec.inventory_api.Movimientos.detalle.Movimiento_Detalle;
import com.granotec.inventory_api.common.enums.TipoMovimiento;
import com.granotec.inventory_api.common.enums.TypeOperation;
import com.granotec.inventory_api.common.model.BaseEntity;
import com.granotec.inventory_api.customer.Customer;
import com.granotec.inventory_api.storage.Storage;
import com.granotec.inventory_api.vendor.Vendor;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "movimientos")
public class Movimiento extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private LocalDate fechaDocumento;

    @ManyToOne
    @JoinColumn(name = "almacen_id")
    private Storage almacen;

    @Enumerated(EnumType.STRING)
    private TipoMovimiento tipoMovimiento;

    @Enumerated(EnumType.STRING)
    private TypeOperation tipoOperacion;

    private String nroFactura;

    @ManyToOne
    @JoinColumn(name = "proveedor_id")
    private Vendor proveedor;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Customer destino_cliente;

    @Column(precision = 19, scale = 4)
    private BigDecimal totalSoles;

    @Column(precision = 19, scale = 4)
    private BigDecimal totalDolares;

     @OneToMany(mappedBy = "movimiento", cascade = CascadeType.ALL, orphanRemoval = true)
      private List<Movimiento_Detalle> detalles;

}
