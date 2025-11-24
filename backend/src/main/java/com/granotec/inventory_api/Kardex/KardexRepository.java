package com.granotec.inventory_api.Kardex;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KardexRepository extends JpaRepository<Kardex, Integer> {

    // Movimientos por producto
    List<Kardex> findByProductoIdOrderByFechaMovimientoAsc(Integer productoId);

    Page<Kardex> findByProductoId(Integer productoId, Pageable pageable);

    // Movimientos por lote
    List<Kardex> findByLoteIdOrderByFechaMovimientoAsc(Integer loteId);

    Page<Kardex> findByLoteId(Integer loteId, Pageable pageable);

    // Kardex por almacén
    List<Kardex> findByAlmacenId(Integer almacenId);

    List<Kardex> findByAlmacenIdOrderByFechaMovimiento(Integer almacenId);

    Page<Kardex> findByAlmacenId(Integer almacenId, Pageable pageable);

    List<Kardex> findByTipoOperacion(com.granotec.inventory_api.common.enums.TypeOperation tipoOperacion);
}
