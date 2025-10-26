package com.granotec.inventory_api.dispatch;

import com.granotec.inventory_api.car.Car;
import com.granotec.inventory_api.carrier.Carrier;
import com.granotec.inventory_api.common.enums.Status;
import com.granotec.inventory_api.common.model.BaseEntity;
import com.granotec.inventory_api.dispatch.details_dispatch.DetailsDispatch;
import com.granotec.inventory_api.driver.Driver;
import com.granotec.inventory_api.ov.Ov;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "despacho")
public class Dispatch extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ordenventa", nullable = false)
    private Ov ov;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_transportista")
    private Carrier carrier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_chofer")
    private Driver driver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_vehiculo", nullable = false)
    private Car car;

    @Column(name = "fecha_despacho", nullable = false)
    private LocalDate fechaDespacho;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 15)
    private Status estado;

    @OneToMany(mappedBy = "despacho", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetailsDispatch> detalles;

}
