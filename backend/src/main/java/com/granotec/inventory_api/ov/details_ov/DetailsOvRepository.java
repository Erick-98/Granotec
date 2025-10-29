package com.granotec.inventory_api.ov.details_ov;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DetailsOvRepository extends JpaRepository<Details_ov, Integer> {
}

