package com.granotec.inventory_api.transportation_assignment.carrier;

import com.granotec.inventory_api.common.model.Person;
import com.granotec.inventory_api.transportation_assignment.driver.Driver;
import com.granotec.inventory_api.transportation_assignment.Transp_Assignment;
import com.granotec.inventory_api.transportation_assignment.car.Car;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "transportista")
public class Carrier extends Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "razon_social", nullable = false,length = 150)
    private String razonSocial;

    @Column(unique = true)
    private String nroDocumento;

    @OneToMany(mappedBy = "carrier", cascade = CascadeType.ALL)
    private List<Driver> drivers;

    @OneToMany(mappedBy = "carrier", cascade = CascadeType.ALL)
    private List<Car> cars;

    @OneToMany(mappedBy = "carrier")
    private List<Transp_Assignment> asignaciones;
}

