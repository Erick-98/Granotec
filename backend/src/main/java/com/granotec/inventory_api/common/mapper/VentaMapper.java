package com.granotec.inventory_api.common.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import com.granotec.inventory_api.OrdenVenta.OrdenVenta;
import com.granotec.inventory_api.ventas.dto.VentaResponse;

@Mapper(componentModel = "spring")
public interface VentaMapper {
    VentaMapper INSTANCE = Mappers.getMapper(VentaMapper.class);
    VentaResponse toDto(OrdenVenta entity);
}

