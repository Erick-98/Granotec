package com.granotec.inventory_api.vendor;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface VendorRepository extends JpaRepository<Vendor,Long> {
    Optional<Vendor> findByEmail(String email);
    Optional<Vendor> findByDocumento(String documento);
}
