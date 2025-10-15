package com.granotec.inventory_api.vendor;

import com.granotec.inventory_api.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "proveedor")
public class Vendor extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    private String tipoDocumento;

    @Column(unique = true)
    private String documento;

    private String direccion;

    private String telefono;

    @Column(unique = true)
    private String email;



}
