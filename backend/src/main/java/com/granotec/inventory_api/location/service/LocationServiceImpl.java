package com.granotec.inventory_api.location.service;

import com.granotec.inventory_api.common.exception.ResourceNotFoundException;
import com.granotec.inventory_api.location.dto.DepartmentDTO;
import com.granotec.inventory_api.location.dto.DistrictDTO;
import com.granotec.inventory_api.location.dto.ProvinceDTO;
import com.granotec.inventory_api.location.repository.DepartmentRepository;
import com.granotec.inventory_api.location.repository.DistrictRepository;
import com.granotec.inventory_api.location.repository.ProvinceRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LocationServiceImpl implements LocationService{

    private final DepartmentRepository departmentRepository;
    private final ProvinceRepository provinceRepository;
    private final DistrictRepository districtRepository;

    @Override
    public List<DepartmentDTO> listarDepartamentos() {
        return departmentRepository.findAll()
                .stream()
                .map(d -> new DepartmentDTO(d.getId(),d.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public List<ProvinceDTO> listarProvinciasPorDepartamento(Integer departamentoId) {

        departmentRepository.findById(departamentoId)
                .orElseThrow(() -> new ResourceNotFoundException("El departamento no existe"));

        return provinceRepository.findByDepartmentId(departamentoId)
                .stream()
                .map(p -> ProvinceDTO.builder()
                        .id(p.getId())
                        .name(p.getName())
                        .departmentId(p.getDepartment() != null ? p.getDepartment().getId() : null)
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<DistrictDTO> listarDistritosPorProvincia(Integer provinciaId) {

        provinceRepository.findById(provinciaId).orElseThrow(() -> new ResourceNotFoundException("La provincia no existe"));

        return districtRepository.findByProvinceId(provinciaId)
                .stream()
                .map(dist -> new DistrictDTO(dist.getId(),dist.getName(),dist.getProvince() != null ? dist.getProvince().getId() : null))
                .collect(Collectors.toList());
    }
}
