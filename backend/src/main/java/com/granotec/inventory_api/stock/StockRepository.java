package com.granotec.inventory_api.stock;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.math.BigDecimal;
import java.util.List;
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

    // Atomic update using native query: decrease by stock id if sufficient
    @Modifying
    @Query(value = "UPDATE stock SET cantidad = cantidad - :qty WHERE id = :id AND cantidad >= :qty", nativeQuery = true)
    int decreaseByIdIfSufficient(@Param("id") Long id, @Param("qty") BigDecimal qty);

    // Atomic update using native query: decrease by almacen+producto+lote if sufficient
    @Modifying
    @Query(value = "UPDATE stock SET cantidad = cantidad - :qty WHERE id_almacen = :almacenId AND id_producto = :productoId AND ((:lote IS NULL AND lote IS NULL) OR lote = :lote) AND cantidad >= :qty", nativeQuery = true)
    int decreaseByAlmacenProductoLoteIfSufficient(@Param("almacenId") Long almacenId,
                                                   @Param("productoId") Integer productoId,
                                                   @Param("lote") String lote,
                                                   @Param("qty") BigDecimal qty);

    // Helper: encontrar primer stock disponible para un producto (orden por id asc)
    Optional<Stock> findFirstByProductoIdOrderByIdAsc(Integer productoId);

    // Lista de stocks por producto
    List<Stock> findByProductoId(Integer productoId);

    // Suma total de cantidad por producto
    @Query("select coalesce(sum(s.cantidad),0) from Stock s where s.producto.id = :productoId")
    BigDecimal sumCantidadByProductoId(@Param("productoId") Integer productoId);
}
