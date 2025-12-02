package com.granotec.inventory_api.transportation_assignment.driver;


import com.granotec.inventory_api.common.model.Person;
import com.granotec.inventory_api.transportation_assignment.Transp_Assignment;
import com.granotec.inventory_api.transportation_assignment.carrier.Carrier;
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
@Table(name = "chofer")
public class Driver extends Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 40)
    private String apellidos;

    @Column(unique = true)
    private String nroDocumento;

    @ManyToOne
    @JoinColumn(name = "id_transportista")
    private Carrier carrier;

    @OneToMany(mappedBy = "driver")
    private List<Transp_Assignment> asignaciones;
}
