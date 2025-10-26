package com.granotec.inventory_api.carrier;

import com.granotec.inventory_api.car.Car;
import com.granotec.inventory_api.common.model.Person;
import com.granotec.inventory_api.driver.Driver;
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
    private int id;

    @Column(name = "razon_social", nullable = false,length = 150)
    private String razonSocial;

    @OneToMany(mappedBy = "carrier")
    private List<Driver> drivers;

    @OneToMany(mappedBy = "carrier")
    private List<Car> cars;
}

