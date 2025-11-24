package com.granotec.inventory_api.OrdenProduccion;

import com.granotec.inventory_api.common.enums.ProducciónStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdenProduccionRepository extends JpaRepository<OrdenProduccion, Integer> {
    // órdenes pendientes
    List<OrdenProduccion> findByEstado(ProducciónStatus estado);

    // buscar por producto y estado
    List<OrdenProduccion> findByProductoIdAndEstado(Long productoId, String estado);
}
