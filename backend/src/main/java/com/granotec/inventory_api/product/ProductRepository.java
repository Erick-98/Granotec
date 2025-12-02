package com.granotec.inventory_api.product;

import com.granotec.inventory_api.common.enums.TypeProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    Page<Product> findByNombreComercialContainingIgnoreCaseOrCodeContainingIgnoreCase(String nombre, String code, Pageable pageable);
    Optional<Product> findByCode(String code);
    List<Product> findByTipoProducto(TypeProduct tipoProducto);
}
