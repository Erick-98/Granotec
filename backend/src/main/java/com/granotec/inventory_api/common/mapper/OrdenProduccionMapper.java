package com.granotec.inventory_api.common.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import com.granotec.inventory_api.OrdenProduccion.OrdenProduccion;
import com.granotec.inventory_api.produccion.dto.OrdenProduccionResponse;

@Mapper(componentModel = "spring")
public interface OrdenProduccionMapper {
    OrdenProduccionMapper INSTANCE = Mappers.getMapper(OrdenProduccionMapper.class);
    OrdenProduccionResponse toDto(OrdenProduccion entity);
}

