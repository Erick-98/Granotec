package com.granotec.inventory_api.Lote;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoteRepository extends JpaRepository<Lote,Integer> {

    Optional<Lote> findByCodigoLote(String codigo);

    // Lotes de un producto ordenados FIFO (por fechaProduccion asc)
    List<Lote> findByProductoIdAndEstadoOrderByFechaProduccionAsc(Integer productoId, String estado);

    // Lotes disponibles (estado = DISPONIBLE)
    List<Lote> findByProductoIdAndEstado(Integer productoId, String estado);

    Lote findByProductoId (Integer productoId);
}
