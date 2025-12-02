package com.granotec.inventory_api.customer.typeCustomer;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface TypeCustomerRepository extends JpaRepository<TypeCustomer, Long> {

    List<TypeCustomer> findAllByIsDeletedFalse();

    boolean existsByNombreIgnoreCaseAndIsDeletedFalse(String nombre);

    Optional<TypeCustomer> findByNombreIgnoreCaseAndIsDeletedFalse(String nombre);
}
