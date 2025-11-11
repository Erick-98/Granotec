package com.granotec.inventory_api.Recojo;

import com.granotec.inventory_api.common.model.BaseEntity;
import com.granotec.inventory_api.Recojo.detalle.RecojoDetalle;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "recojo")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Recojo extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ocNumber;
    private String proveedorCliente;
    private String solicitante;
    private LocalDate fechaRecojo;
    private String horarioRecepcion; // MANANA/TARDE
    private String choferAsignado;
    private String placa;
    private String observaciones;
    private String status;
    private Double kilos;

    @OneToMany(mappedBy = "recojo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecojoDetalle> detalles;
}

