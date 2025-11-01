package com.granotec.inventory_api.dispatch;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface DispatchRepository extends JpaRepository<Dispatch, Integer> {
    Page<Dispatch> findByAsignacionId(Integer asignacionId, Pageable pageable);

    @Query("SELECT COUNT(d) FROM Dispatch d WHERE d.fechaDespacho BETWEEN :from AND :to")
    long countByFechaBetween(@Param("from") LocalDate from, @Param("to") LocalDate to);

    @Query("SELECT COUNT(d) FROM Dispatch d WHERE d.estado = :estado AND d.fechaDespacho BETWEEN :from AND :to")
    long countByEstadoAndFechaBetween(@Param("estado") com.granotec.inventory_api.common.enums.Status estado,
                                      @Param("from") LocalDate from,
                                      @Param("to") LocalDate to);

    // avg delivery days using native MySQL DATEDIFF between created_at and fecha_despacho for delivered dispatches in range
    @Query(value = "SELECT AVG(DATEDIFF(fecha_despacho, created_at)) FROM despacho WHERE fecha_despacho BETWEEN :from AND :to AND estado = :estado", nativeQuery = true)
    Double avgDeliveryDaysByFechaBetweenAndEstado(@Param("from") LocalDate from,
                                                  @Param("to") LocalDate to,
                                                  @Param("estado") String estado);
}
