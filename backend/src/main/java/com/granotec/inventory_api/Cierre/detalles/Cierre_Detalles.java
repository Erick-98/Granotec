package com.granotec.inventory_api.Cierre.detalles;

import com.granotec.inventory_api.Cierre.Cierre;
import com.granotec.inventory_api.common.model.BaseEntity;
import com.granotec.inventory_api.product.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "cierre_detalles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cierre_Detalles extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cierre_id")
    private Cierre cierre;

    @ManyToOne
    @JoinColumn(name = "producto_id")
    private Product producto;

    private Double cantidadStock;
    private Double sacosCajas;
    private String presentacion;
    private Double cantidadPorPresentacion;
    private Double noConformeKg;
    private Double vencidosKg;
    private Double pendienteEliminacionKg;
    private Double almacenFacturadosKg;
    private Double dosimetriaKg;
    private Double totalFisicoKg;
    private Double totalSistemaKg;
    private Double diferenciaKg;
    private String loteObservaciones;
    private String empaque;

}
