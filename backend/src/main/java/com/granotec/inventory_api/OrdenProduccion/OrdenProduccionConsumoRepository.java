package com.granotec.inventory_api.OrdenProduccion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrdenProduccionConsumoRepository extends JpaRepository<OrdenProduccionConsumo, Long> {
    List<OrdenProduccionConsumo> findByOrdenProduccionId(Integer ordenId);
}

