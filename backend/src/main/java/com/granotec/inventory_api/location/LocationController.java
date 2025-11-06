package com.granotec.inventory_api.location;

import com.granotec.inventory_api.location.dto.DepartmentDTO;
import com.granotec.inventory_api.location.dto.DistrictDTO;
import com.granotec.inventory_api.location.dto.ProvinceDTO;
import com.granotec.inventory_api.location.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/location")
public class LocationController {

    private final LocationService service;

    @GetMapping("/departamentos")
    public ResponseEntity<List<DepartmentDTO>> listarDepartamentos() {
        return ResponseEntity.ok(service.listarDepartamentos());
    }

    @GetMapping("/provincias/{departamentoId}")
    public ResponseEntity<List<ProvinceDTO>> listarProvincias(@PathVariable Integer departamentoId) {
        return ResponseEntity.ok(service.listarProvinciasPorDepartamento(departamentoId));
    }

    @GetMapping("/distritos/{provinciaId}")
    public ResponseEntity<List<DistrictDTO>> listarDistritos(@PathVariable Integer provinciaId) {
        return ResponseEntity.ok(service.listarDistritosPorProvincia(provinciaId));
    }
}
