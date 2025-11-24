package com.granotec.inventory_api.ConsumoProduccion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConsumoProduccionRepository extends JpaRepository<ConsumoProduccion, Integer> {

    // consumos por orden de producción (trazabilidad)
    List<ConsumoProduccion> findByOrdenProduccionId(Long ordenProduccionId);

    // consumos por insumo (qué órdenes usaron un insumo)
    List<ConsumoProduccion> findByInsumoId(Long insumoId);


}
