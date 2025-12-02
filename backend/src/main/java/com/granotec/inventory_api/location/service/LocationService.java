package com.granotec.inventory_api.location.service;

import com.granotec.inventory_api.location.dto.DepartmentDTO;
import com.granotec.inventory_api.location.dto.DistrictDTO;
import com.granotec.inventory_api.location.dto.ProvinceDTO;
import java.util.List;

public interface LocationService {

    List<DepartmentDTO> listarDepartamentos();
    List<ProvinceDTO> listarProvinciasPorDepartamento(Integer departamentoId);
    List<DistrictDTO> listarDistritosPorProvincia(Integer provinciaId);
}
