package com.granotec.inventory_api.OrdenVenta;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OrdenVentaRepository extends JpaRepository<OrdenVenta, Integer> {
    List<OrdenVenta> findByFechaBetween(LocalDate desde, LocalDate hasta);
}
