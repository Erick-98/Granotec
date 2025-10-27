package com.granotec.inventory_api.stock;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {
    Optional<Stock> findByAlmacenIdAndProductoIdAndLote(Long almacenId, Integer productoId, String lote);

    // Método para obtener el registro con bloqueo pesimista (para operaciones concurrentes seguras)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Stock s where s.almacen.id = :almacenId and s.producto.id = :productoId and (s.lote = :lote or (:lote is null and s.lote is null))")
    Optional<Stock> findByAlmacenIdAndProductoIdAndLoteForUpdate(@Param("almacenId") Long almacenId,
                                                                  @Param("productoId") Integer productoId,
                                                                  @Param("lote") String lote);
}
