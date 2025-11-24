package com.granotec.inventory_api.vendor;

import com.granotec.inventory_api.common.enums.CondicionPago;
import com.granotec.inventory_api.common.enums.Currency;
import com.granotec.inventory_api.common.model.Person;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "proveedor")
public class Vendor extends Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String razonSocial;

    @Column(unique = true)
    private String nroDocumento;

    @Column(length = 500)
    private String notas;

    @Enumerated(EnumType.STRING)
    @Column(name = "moneda", nullable = false)
    private Currency moneda;

    @Enumerated(EnumType.STRING)
    @Column(name = "condicion_pago")
    private CondicionPago condicionPago;

}
