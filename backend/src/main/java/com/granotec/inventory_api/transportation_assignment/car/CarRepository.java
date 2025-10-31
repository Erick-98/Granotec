package com.granotec.inventory_api.transportation_assignment.car;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {
    boolean existsByPlaca(String placa);
    Page<Car> findAllByIsDeletedFalse(Pageable pageable);
}

