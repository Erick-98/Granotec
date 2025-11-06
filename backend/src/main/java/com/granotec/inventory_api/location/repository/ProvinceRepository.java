package com.granotec.inventory_api.location.repository;

import com.granotec.inventory_api.location.entity.Province;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProvinceRepository extends JpaRepository<Province, Integer> {
    List<Province> findByDepartmentId(Integer departmentId);
}
