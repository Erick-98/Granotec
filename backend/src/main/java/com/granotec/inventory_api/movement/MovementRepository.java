package com.granotec.inventory_api.movement;

import com.granotec.inventory_api.movement.projection.MovementListProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface MovementRepository extends JpaRepository<Movement, Long> {

    @Query("SELECT m.id as id, m.fechaMovimiento as fechaMovimiento, m.numeroFactura as numeroFactura, m.tipoMovimiento as tipoMovimiento, m.tipoOperacion as tipoOperacion, m.estado as estado, "
            + "m.almacenOrigen.id as almacenOrigenId, m.almacenDestino.id as almacenDestinoId, d.product.id as productId, d.product.name as productName, d.lote as lote, d.cantidad as cantidad, d.total as total "
            + "FROM Movement m JOIN m.detalles d "
            + "WHERE (:fromDate IS NULL OR m.fechaMovimiento >= :fromDate) "
            + "AND (:toDate IS NULL OR m.fechaMovimiento <= :toDate) "
            + "AND (:almacenId IS NULL OR m.almacenOrigen.id = :almacenId OR m.almacenDestino.id = :almacenId) "
            + "AND (:productId IS NULL OR d.product.id = :productId) "
            + "AND (:tipoOperacion IS NULL OR m.tipoOperacion = :tipoOperacion) ")
    Page<MovementListProjection> findByFilters(@Param("fromDate") LocalDate fromDate,
                                               @Param("toDate") LocalDate toDate,
                                               @Param("almacenId") Long almacenId,
                                               @Param("productId") Integer productId,
                                               @Param("tipoOperacion") OperationType tipoOperacion,
                                               Pageable pageable);
}
