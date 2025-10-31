package com.granotec.inventory_api.salesperson;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalespersonRepository extends JpaRepository<Salesperson, Integer> {
    Page<Salesperson> findByNameContainingIgnoreCaseOrNroDocumentoContainingIgnoreCase(String name, String nroDocumento, Pageable pageable);
}
