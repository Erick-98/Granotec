package com.granotec.inventory_api.common.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import com.granotec.inventory_api.Lote.Lote;
import com.granotec.inventory_api.inventario.dto.StockLoteResponse;

@Mapper(componentModel = "spring")
public interface LoteMapper {
    LoteMapper INSTANCE = Mappers.getMapper(LoteMapper.class);
    StockLoteResponse toStockLoteDto(Lote entity);
}

