package com.granotec.inventory_api.transportation_assignment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Transp_AssignmentRepository extends JpaRepository<Transp_Assignment, Integer> {

    Page<Transp_Assignment> findByCarrier_IdAndIsDeletedFalse(Integer carrierId, Pageable pageable);

    Page<Transp_Assignment> findByDriver_IdAndIsDeletedFalse(Long driverId, Pageable pageable);
}
