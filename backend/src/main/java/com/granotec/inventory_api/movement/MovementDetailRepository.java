package com.granotec.inventory_api.movement;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovementDetailRepository extends JpaRepository<MovementDetail, Long> {
}

