package com.granotec.inventory_api.vendor;

import com.granotec.inventory_api.common.model.BaseEntity;
import com.granotec.inventory_api.common.enums.VendorDocumentType;
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VendorDocumentType tipoDocumento;

    @Column(unique = true, nullable = false)
    private String documento;

    private String direccion;

    private String telefono;

    @Column(unique = true)
    private String email;



}
