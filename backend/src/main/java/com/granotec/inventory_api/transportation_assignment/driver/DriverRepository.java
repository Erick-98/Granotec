package com.granotec.inventory_api.transportation_assignment.driver;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {
    Page<Driver> findAllByIsDeletedFalse(Pageable pageable);
}

