package com.granotec.inventory_api.Kardex;

import com.granotec.inventory_api.common.enums.TypeOperation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface KardexRepository extends JpaRepository<Kardex, Integer> {

    Page<Kardex> findByProductoId(Integer productoId, Pageable pageable);
    Page<Kardex> findByLoteId(Integer loteId, Pageable pageable);
    Page<Kardex> findByAlmacenId(Integer almacenId, Pageable pageable);
    List<Kardex> findByTipoOperacion(TypeOperation tipoOperacion);


    @Query("SELECT k FROM Kardex k " +
            "LEFT JOIN  k.producto p " +
            "LEFT JOIN  k.almacen a " +
            "LEFT JOIN  k.usuario u " +
            "LEFT JOIN  k.lote l " +
            "LEFT JOIN  k.orden o " +
            "WHERE (:productoId IS NULL OR p.id = :productoId) AND " +
            "(:almacenId IS NULL OR a.id= :almacenId) AND " +
            "(:desde IS NULL OR k.fechaMovimiento >= :desde) AND " +
            "(:hasta IS NULL OR k.fechaMovimiento <= :hasta) ")
    Page<Kardex> search(@Param("productoId") Integer productoId,
                        @Param("almacenId") Long almacenId,
                        @Param("desde") LocalDate desde,
                        @Param("hasta") LocalDate hasta,
                        Pageable pageable);


}
