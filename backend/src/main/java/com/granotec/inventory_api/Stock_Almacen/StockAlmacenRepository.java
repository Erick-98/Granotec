package com.granotec.inventory_api.Stock_Almacen;

import com.granotec.inventory_api.product.Product;
import com.granotec.inventory_api.storage.Storage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface StockAlmacenRepository extends JpaRepository<StockAlmacen, Long> {

    // Buscar stock por producto y almacén
    List<StockAlmacen> findByProductoIdAndAlmacenId(Integer productoId, Long almacenId);

    // Stock total por producto en todos los almacenes
    @Query("SELECT SUM(s.cantidad) FROM StockAlmacen s WHERE s.producto.id = :productoId")
    BigDecimal getTotalStockByProducto(@Param("productoId") Integer productoId);

    Optional<StockAlmacen> findByAlmacenAndProducto(Storage almacen, Product producto);

    // Métodos de paginación
    Page<StockAlmacen> findByProductoIdAndAlmacenIdAndIsDeletedFalse(Integer productoId, Long almacenId, Pageable pageable);
    Page<StockAlmacen> findByProductoIdAndIsDeletedFalse(Integer productoId, Pageable pageable);
    Page<StockAlmacen> findByAlmacenIdAndIsDeletedFalse(Long almacenId, Pageable pageable);


}
