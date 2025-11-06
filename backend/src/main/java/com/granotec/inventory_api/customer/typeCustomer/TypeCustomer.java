package com.granotec.inventory_api.customer.typeCustomer;

import com.granotec.inventory_api.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tipo_cliente", uniqueConstraints = @UniqueConstraint(columnNames = "nombre"))
public class TypeCustomer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String nombre;

    @Column(length = 500)
    private String descripcion;
}
