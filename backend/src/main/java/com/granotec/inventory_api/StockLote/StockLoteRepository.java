package com.granotec.inventory_api.StockLote;


import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockLoteRepository extends JpaRepository<StockLote, Integer> {

    List<StockLote> findByLoteProductoIdAndAlmacenId(Integer productoId, Long almacenId);

    List<StockLote> findByLoteProductoId(Integer productoId);

    Optional<StockLote> findByLoteIdAndAlmacenId(Long loteId, Long almacenId);

    // Query FIFO
    @Query("SELECT s FROM StockLote s " +
            "WHERE s.lote.producto.id = :productoId " +
            "AND s.cantidadDisponible > 0 " +
            "AND s.almacen.id = :almacenId " +
            "AND s.lote.estado = 'DISPONIBLE' " +
            "ORDER BY s.lote.fechaProduccion ASC")
    List<StockLote> findAvailableByProductoAndAlmacenFIFO(@Param("productoId") Long productoId,
                                                          @Param("almacenId") Long almacenId);

    // Locking
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM StockLote s " +
            "WHERE s.lote.producto.id = :productoId " +
            "AND s.cantidadDisponible > 0 " +
            "AND s.almacen.id = :almacenId " +
            "AND s.lote.estado = 'DISPONIBLE' " +
            "ORDER BY s.lote.fechaProduccion ASC")
    List<StockLote> findAvailableByProductoAndAlmacenForUpdate(
            @Param("productoId") Integer productoId,
            @Param("almacenId") Long almacenId
    );

    Page<StockLote> findByLoteProductoIdAndAlmacenId(Integer productoId, Long almacenId, Pageable pageable);
    Page<StockLote> findByLoteProductoId(Integer productoId, Pageable pageable);
    Page<StockLote> findByAlmacenId(Long almacenId, Pageable pageable);
}
