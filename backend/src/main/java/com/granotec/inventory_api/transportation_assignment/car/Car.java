package com.granotec.inventory_api.transportation_assignment.car;

import com.granotec.inventory_api.common.model.BaseEntity;
import com.granotec.inventory_api.transportation_assignment.carrier.Carrier;
import com.granotec.inventory_api.transportation_assignment.Transp_Assignment;
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
@Table(name = "carro")
public class Car extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "placa", nullable = false, unique = true, length = 7)
    private String placa;

    @Column(name = "marca", length = 40)
    private String marca;

    @ManyToOne
    @JoinColumn(name = "id_transportista", nullable = false)
    private Carrier carrier;

    @OneToMany(mappedBy = "car")
    private List<Transp_Assignment> asignaciones;
}
