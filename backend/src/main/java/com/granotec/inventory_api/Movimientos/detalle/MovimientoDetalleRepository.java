package com.granotec.inventory_api.Movimientos.detalle;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovimientoDetalleRepository extends JpaRepository<Movimiento_Detalle, Long> {
}

