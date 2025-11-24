package com.granotec.inventory_api.common.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import com.granotec.inventory_api.inventario.dto.StockAlmacenResponse;
import com.granotec.inventory_api.Stock_Almacen.StockAlmacen;

@Mapper(componentModel = "spring")
public interface StockAlmacenMapper {
    StockAlmacenMapper INSTANCE = Mappers.getMapper(StockAlmacenMapper.class);
    StockAlmacenResponse toDto(StockAlmacen entity);
}

