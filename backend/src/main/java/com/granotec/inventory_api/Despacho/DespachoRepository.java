package com.granotec.inventory_api.Despacho;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DespachoRepository extends JpaRepository<Despacho, Long> {
}

