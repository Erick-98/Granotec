package com.granotec.inventory_api.OrdenProduccion;

import com.granotec.inventory_api.ConsumoProduccion.ConsumoProduccion;
import com.granotec.inventory_api.Lote.Lote;
import com.granotec.inventory_api.OrdenProduccion.ListaMaterial.ListaMaterial;
import com.granotec.inventory_api.common.enums.EstadoLaboratorio;
import com.granotec.inventory_api.common.enums.ProduccionStatus;
import com.granotec.inventory_api.common.model.BaseEntity;
import com.granotec.inventory_api.product.Product;
import com.granotec.inventory_api.storage.Storage;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "orden_produccion")
public class OrdenProduccion extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String numero;

    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private Product producto;

    @Column(nullable = false)
    private BigDecimal cantidadProgramada;

    private BigDecimal cantidadProducida;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_calidad")
    private EstadoLaboratorio estadoLaboratorio;

    @ManyToOne
    @JoinColumn(name = "lista_material_id")
    private ListaMaterial listaMaterial;

    @ManyToOne
    @JoinColumn(name = "almacen_destino_id", nullable = false)
    private Storage almacenDestino;

    private LocalDate fechaCreacion;

    private LocalDate fechaInicio;

    private LocalDate fechaFin;

    @OneToMany(mappedBy = "ordenProduccion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ConsumoProduccion> consumos = new ArrayList<>();

    @OneToMany(mappedBy = "ordenProduccion", cascade = CascadeType.ALL)
    private List<Lote> lote = new ArrayList<>();

    private BigDecimal costoEstimado;
    private BigDecimal costoReal;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado",nullable = false, length = 30)
    private ProduccionStatus estado;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDate.now();
    }
}
