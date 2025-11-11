package com.granotec.inventory_api.Recojo.detalle;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecojoDetalleRepository extends JpaRepository<RecojoDetalle, Long> {
}

