package com.granotec.inventory_api.OrdenProduccion;

import com.granotec.inventory_api.common.enums.ProduccionStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrdenProduccionRepository extends JpaRepository<OrdenProduccion, Integer> {
    // órdenes pendientes
    List<OrdenProduccion> findByEstado(ProduccionStatus estado);

    Optional<OrdenProduccion> findByNumero(String numero);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT o FROM OrdenProduccion o WHERE o.id = :id")
    Optional<OrdenProduccion> pessimisticLock(@Param("id") Integer id);
}
