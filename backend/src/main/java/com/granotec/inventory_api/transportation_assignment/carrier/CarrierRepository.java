package com.granotec.inventory_api.transportation_assignment.carrier;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CarrierRepository extends JpaRepository<Carrier, Integer> {
    Page<Carrier> findAllByIsDeletedFalse(Pageable pageable);
}

