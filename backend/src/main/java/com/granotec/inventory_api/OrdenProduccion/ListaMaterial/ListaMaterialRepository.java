package com.granotec.inventory_api.OrdenProduccion.ListaMaterial;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.Optional;

@Repository
public interface ListaMaterialRepository extends JpaRepository<ListaMaterial, Integer> {
    Optional<ListaMaterial> findByProductoId(Integer productoId);
}
