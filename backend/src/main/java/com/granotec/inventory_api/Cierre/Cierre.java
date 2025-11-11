package com.granotec.inventory_api.Cierre;

import com.granotec.inventory_api.common.model.BaseEntity;
import com.granotec.inventory_api.storage.Storage;
import com.granotec.inventory_api.Cierre.detalles.Cierre_Detalles;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "cierre")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cierre extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate fechaCierre;

    @ManyToOne
    @JoinColumn(name = "almacen_id")
    private Storage almacen;

    @OneToMany(mappedBy = "cierre", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Cierre_Detalles> detalles;

}
