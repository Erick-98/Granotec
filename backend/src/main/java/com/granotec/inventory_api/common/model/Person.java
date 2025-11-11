package com.granotec.inventory_api.common.model;


import com.granotec.inventory_api.common.enums.DocumentType;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public abstract class Person extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column
    private DocumentType tipoDocumento;

    private String direccion;

    @Column
    @Size(max = 9)
    private String telefono;

    @Column
    @Email
    private String email;
}
