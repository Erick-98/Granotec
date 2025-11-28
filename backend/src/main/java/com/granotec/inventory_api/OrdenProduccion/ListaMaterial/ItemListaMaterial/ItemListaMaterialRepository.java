package com.granotec.inventory_api.OrdenProduccion.ListaMaterial.ItemListaMaterial;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemListaMaterialRepository extends JpaRepository<ItemListaMaterial, Integer> {
    List<ItemListaMaterial> findByListaMaterialId(Integer listaMaterialId);
}
