package com.granotec.inventory_api.customer;


import com.granotec.inventory_api.common.model.Person;
import com.granotec.inventory_api.ov.Ov;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @OneToMany(mappedBy = "customer")
    private List<Ov> ordenesDeVenta;
}
