package com.granotec.inventory_api.common.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import com.granotec.inventory_api.compras.dto.CompraResponse;
import com.granotec.inventory_api.OrdenCompra.OrdenCompra;

@Mapper(componentModel = "spring")
public interface CompraMapper {
    CompraMapper INSTANCE = Mappers.getMapper(CompraMapper.class);
    CompraResponse toDto(OrdenCompra entity);
}

