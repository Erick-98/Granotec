package com.granotec.inventory_api.transportation_assignment;

import com.granotec.inventory_api.common.model.BaseEntity;
import com.granotec.inventory_api.dispatch.Dispatch;
import com.granotec.inventory_api.transportation_assignment.car.Car;
import com.granotec.inventory_api.transportation_assignment.carrier.Carrier;
import com.granotec.inventory_api.transportation_assignment.driver.Driver;
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
@Table(name = "asignacion_transporte")
public class Transp_Assignment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_transportista",nullable = false)
    private Carrier carrier;

    @ManyToOne
    @JoinColumn(name = "id_chofer", nullable = false)
    private Driver driver;

    @ManyToOne
    @JoinColumn(name = "id_vehiculo", nullable = false)
    private Car car;

    @OneToMany(mappedBy = "asignacion")
    private List<Dispatch> despachos;

}
