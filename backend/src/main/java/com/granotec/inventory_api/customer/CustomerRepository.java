package com.granotec.inventory_api.customer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> , JpaSpecificationExecutor<Customer> {
    Optional<Customer> findByEmail(String email);
    Optional<Customer> findByNroDocumento(String nroDocumento);
}
