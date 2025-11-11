package com.granotec.inventory_api.product.familiaProducto;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface familiaProductoRepository extends JpaRepository<familiaProducto, Long> {

    List<familiaProducto> findAllByIsDeletedFalse();

    boolean existsByNombreIgnoreCaseAndIsDeletedFalse(String nombre);

    Optional<familiaProducto> findByNombreIgnoreCaseAndIsDeletedFalse(String nombre);
}
