package com.granotec.inventory_api.storage;

import com.granotec.inventory_api.storage.entity.Storage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StorageRepository extends JpaRepository<Storage,Long> {
    List<Storage> findAllByIsDeletedFalse();
}
