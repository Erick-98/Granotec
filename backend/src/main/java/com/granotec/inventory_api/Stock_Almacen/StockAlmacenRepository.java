package com.granotec.inventory_api.Stock_Almacen;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface StockAlmacenRepository extends JpaRepository<StockAlmacen, Long> {

    // Buscar stock por producto y almacén
    List<StockAlmacen> findByProductoIdAndAlmacenId(Integer productoId, Long almacenId);

    // Stock total por producto en todos los almacenes
    @Query("SELECT SUM(s.cantidad) FROM StockAlmacen s WHERE s.producto.id = :productoId")
    BigDecimal getTotalStockByProducto(@Param("productoId") Integer productoId);

    // Métodos de paginación
    Page<StockAlmacen> findByProductoIdAndAlmacenId(Integer productoId, Long almacenId, Pageable pageable);
    Page<StockAlmacen> findByProductoId(Integer productoId, Pageable pageable);
    Page<StockAlmacen> findByAlmacenId(Long almacenId, Pageable pageable);
}
