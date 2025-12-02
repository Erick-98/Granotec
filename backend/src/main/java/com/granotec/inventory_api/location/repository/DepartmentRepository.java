package com.granotec.inventory_api.location.repository;

import com.granotec.inventory_api.location.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, Integer> {
}
