package com.granotec.inventory_api.salesperson;

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
@Table(name = "vendedor")
public class Salesperson extends Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 15, unique = true)
    private String apellidos;

    @OneToMany(mappedBy = "salesperson")
    private List<Ov> ordenesDeVenta;
}
