package com.granotec.inventory_api.Insumo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InsumoRepository extends JpaRepository<Insumo, Integer> {

    Optional<Insumo> findByNombre(String nombre);

}
