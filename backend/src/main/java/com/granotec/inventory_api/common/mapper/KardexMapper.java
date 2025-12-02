package com.granotec.inventory_api.common.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import com.granotec.inventory_api.Kardex.Kardex;
import com.granotec.inventory_api.inventario.dto.MovimientoInventarioResponse;

@Mapper(componentModel = "spring")
public interface KardexMapper {
    KardexMapper INSTANCE = Mappers.getMapper(KardexMapper.class);
    MovimientoInventarioResponse toDto(Kardex entity);

    default String mapUsuario(com.granotec.inventory_api.user.User usuario) {
        return usuario != null ? usuario.getName() : null;
    }
}
