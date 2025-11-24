package com.granotec.inventory_api.common.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import com.granotec.inventory_api.ajustes.dto.AjusteResponse;
import com.granotec.inventory_api.Kardex.Kardex;

@Mapper(componentModel = "spring")
public interface AjusteMapper {
    AjusteMapper INSTANCE = Mappers.getMapper(AjusteMapper.class);
    AjusteResponse toDto(Kardex entity);

    default String mapUsuario(com.granotec.inventory_api.user.User usuario) {
        return usuario != null ? usuario.getName() : null;
    }
}
