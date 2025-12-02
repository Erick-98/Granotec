package com.granotec.inventory_api.common.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import com.granotec.inventory_api.inventario.dto.StockLoteResponse;
import com.granotec.inventory_api.StockLote.StockLote;

@Mapper(componentModel = "spring")
public interface StockLoteMapper {
    StockLoteMapper INSTANCE = Mappers.getMapper(StockLoteMapper.class);
    StockLoteResponse toDto(StockLote entity);
}

