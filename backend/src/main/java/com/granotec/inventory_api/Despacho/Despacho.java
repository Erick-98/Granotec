package com.granotec.inventory_api.Despacho;

import com.granotec.inventory_api.common.model.BaseEntity;
import com.granotec.inventory_api.Despacho.detalles.Despacho_detalles;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "despacho")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Despacho extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ordenVenta;
    private String tipoOV;
    private String cliente;
    private String destino;
    private String vendedor;
    private LocalDate fechaDespacho;
    private String choferAsignado;
    private String placa;
    private String transportista;
    private String status;

    @Column(precision = 19, scale = 4)
    private BigDecimal costoFlete;

    @OneToMany(mappedBy = "despacho", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Despacho_detalles> detalles;

}
