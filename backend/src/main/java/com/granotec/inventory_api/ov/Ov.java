package com.granotec.inventory_api.ov;

import com.granotec.inventory_api.common.enums.Currency;
import com.granotec.inventory_api.common.enums.TipoOv;
import com.granotec.inventory_api.common.model.BaseEntity;
import com.granotec.inventory_api.customer.Customer;
import com.granotec.inventory_api.dispatch.Dispatch;
import com.granotec.inventory_api.ov.details_ov.Details_ov;
import com.granotec.inventory_api.salesperson.Salesperson;
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
@Table(name = "orden_venta")
public class Ov extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "numero_documento", nullable = false, unique = true)
    private String numeroDocumento;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_documento", nullable = false)
    private TipoOv tipoDocumento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_vendedor")
    private Salesperson salesperson;

    @Column(name = "fecha_emision", nullable = false)
    private LocalDate fecha;

    @Enumerated(EnumType.STRING)
    @Column(name = "moneda")
    private Currency currency;

    @Column(name = "total", nullable = false, precision = 12, scale = 2)
    private BigDecimal total;

    @OneToMany(mappedBy = "ordenVenta")
    private List<Dispatch> despachos;

    @OneToMany(mappedBy = "ordenVenta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Details_ov> detalle;
}
