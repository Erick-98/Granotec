package com.granotec.inventory_api.driver;


import com.granotec.inventory_api.carrier.Carrier;
import com.granotec.inventory_api.common.model.Person;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @Column(nullable = false, length = 15, unique = true)
    private String apellidos;

    @ManyToOne
    @JoinColumn(name = "carrier_id")
    private Carrier carrier;
}
