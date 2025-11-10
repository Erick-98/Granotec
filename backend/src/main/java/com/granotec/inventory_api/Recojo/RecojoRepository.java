package com.granotec.inventory_api.Recojo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecojoRepository extends JpaRepository<Recojo, Long> {
}

