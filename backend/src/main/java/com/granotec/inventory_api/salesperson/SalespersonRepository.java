package com.granotec.inventory_api.salesperson;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SalespersonRepository extends JpaRepository<Salesperson, Integer> {
    Optional<Salesperson> findByEmail(String email);
    Optional<Salesperson> findByNroDocumento(String nroDocumento);
}
