package com.granotec.inventory_api.OrdenVenta.detalles;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleOrdenVentaRepository extends JpaRepository<DetalleOrdenVenta, Integer> {
    List<DetalleOrdenVenta> findByOrdenVentaId(Integer ventaId);

    // Buscar lineas por lote (trazabilidad)
    List<DetalleOrdenVenta> findByLoteId(Integer loteId);
}
