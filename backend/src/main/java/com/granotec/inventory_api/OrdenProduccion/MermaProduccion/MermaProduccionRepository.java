package com.granotec.inventory_api.OrdenProduccion.MermaProduccion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MermaProduccionRepository extends JpaRepository<MermaProduccion,Integer>{ }
