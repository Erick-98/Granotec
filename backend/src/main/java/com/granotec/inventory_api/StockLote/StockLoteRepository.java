package com.granotec.inventory_api.StockLote;


import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface StockLoteRepository extends JpaRepository<StockLote, Integer> {

    List<StockLote> findByLoteProductoIdAndAlmacenId(Integer productoId, Long almacenId);

    List<StockLote> findByLoteProductoId(Integer productoId);

    Optional<StockLote> findByLoteIdAndAlmacenId(Long loteId, Long almacenId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM StockLote s " +
            "WHERE s.lote.producto.id = :productoId " +
            "AND s.cantidadDisponible > 0 " +
            "AND s.almacen.id = :almacenId " +
            "AND s.lote.estado = 'DISPONIBLE' " +
            "ORDER BY s.lote.fechaProduccion ASC")
    List<StockLote> findAvailableByProductoAndAlmacenForUpdate(@Param("productoId") Integer productoId, @Param("almacenId") Long almacenId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM StockLote s " +
            "WHERE s.lote.producto.id = :productoId " +
            "AND s.cantidadDisponible > 0 " +
            "AND s.lote.estado = 'DISPONIBLE' " +
            "ORDER BY s.lote.fechaProduccion ASC")
    List<StockLote> findAvailableByProductoForUpdate(@Param("productoId") Integer productoId);

    Page<StockLote> findByLoteProductoIdAndAlmacenId(Integer productoId, Long almacenId, Pageable pageable);
    Page<StockLote> findByLoteProductoId(Integer productoId, Pageable pageable);
    Page<StockLote> findByAlmacenId(Long almacenId, Pageable pageable);

    @Query("SELECT coalesce(sum(s.cantidadDisponible),0) FROM StockLote s where s.lote.producto.id = :productoId and s.almacen.id = :almacenId")
    BigDecimal sumDisponibleByProductoAndAlmacen(@Param("productoId") Integer productoId, @Param("almacenId") Long almacenId);

    @Query("select coalesce(sum(s.cantidadDisponible),0) from StockLote s where s.lote.producto.id = :productoId")
    BigDecimal sumDisponibleByProducto(@Param("productoId") Integer productoId);

    Optional<StockLote> findFirstByLoteIdAndIsDeletedFalse(Long loteId);

    /**
     * Calcula el precio promedio ponderado de un producto en un almacén específico
     * basándose en los lotes disponibles (con stock > 0)
     */
    @Query("SELECT CASE WHEN SUM(s.cantidadDisponible) > 0 " +
            "THEN SUM(s.cantidadDisponible * s.lote.costoUnitario) / SUM(s.cantidadDisponible) " +
            "ELSE 0 END " +
            "FROM StockLote s " +
            "WHERE s.lote.producto.id = :productoId " +
            "AND s.almacen.id = :almacenId " +
            "AND s.cantidadDisponible > 0 " +
            "AND s.lote.estado = 'DISPONIBLE'")
    BigDecimal calcularPrecioPromedioPonderado(@Param("productoId") Integer productoId,
                                                @Param("almacenId") Long almacenId);

    /**
     * Calcula el precio promedio ponderado de un producto en todos los almacenes
     */
    @Query("SELECT CASE WHEN SUM(s.cantidadDisponible) > 0 " +
            "THEN SUM(s.cantidadDisponible * s.lote.costoUnitario) / SUM(s.cantidadDisponible) " +
            "ELSE 0 END " +
            "FROM StockLote s " +
            "WHERE s.lote.producto.id = :productoId " +
            "AND s.cantidadDisponible > 0 " +
            "AND s.lote.estado = 'DISPONIBLE'")
    BigDecimal calcularPrecioPromedioPonderadoGeneral(@Param("productoId") Integer productoId);
}

