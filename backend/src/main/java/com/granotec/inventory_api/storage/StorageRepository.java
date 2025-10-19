package com.granotec.inventory_api.storage;

import com.granotec.inventory_api.storage.entity.Storage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StorageRepository extends JpaRepository<Storage,Long> {
    List<Storage> findAllByIsDeletedFalse();

    boolean existsByNombreIgnoreCaseAndIsDeletedFalse(String nombre);

    Optional<Storage> findByNombreIgnoreCaseAndIsDeletedFalse(String nombre);

}
