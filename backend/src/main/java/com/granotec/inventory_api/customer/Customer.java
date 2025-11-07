package com.granotec.inventory_api.customer;


import com.granotec.inventory_api.common.enums.CondicionPago;
import com.granotec.inventory_api.common.model.Person;
import com.granotec.inventory_api.customer.typeCustomer.TypeCustomer;
import com.granotec.inventory_api.location.entity.Department;
import com.granotec.inventory_api.location.entity.District;
import com.granotec.inventory_api.location.entity.Province;
import com.granotec.inventory_api.ov.Ov;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cliente")
public class Customer extends Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100)
    private String name;

    @Column(length = 40)
    private String apellidos;

    @Column(length = 150, unique = true)
    private String razonSocial;

    @Column(length = 150)
    private String zona;

    @ManyToOne
    @JoinColumn(name = "distrito_id")
    private District distrito;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_cliente_id", nullable = false)
    private TypeCustomer tipoCliente;

    @Column(length = 100)
    private String rubro;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private CondicionPago condicionPago;

    @Column(precision = 12, scale = 2)
    private BigDecimal limiteDolares;

    @Column(precision = 12, scale = 2)
    private BigDecimal limiteCreditoSoles;

    @Column(columnDefinition = "TEXT")
    private String notas;

    @OneToMany(mappedBy = "customer")
    private List<Ov> ordenesDeVenta;

    @Transient
    public Province getProvincia(){
        return distrito != null ? distrito.getProvince() : null;
    }

    @Transient
    public Department getDepartamento(){
        Province provincia = getProvincia();
        return provincia != null ? provincia.getDepartment() : null;
    }
}
