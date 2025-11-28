package com.granotec.inventory_api.inventario.Mapper;

import com.granotec.inventory_api.StockLote.StockLote;
import com.granotec.inventory_api.inventario.dto.StockDisponibleResponse;
import org.springframework.stereotype.Component;

@Component
public class StockLoteMapper {

    public StockDisponibleResponse toDto(StockLote stock){
        if(stock == null) return null;

        StockDisponibleResponse dto = new StockDisponibleResponse();
        dto.setProductoId(Long.valueOf(stock.getLote().getProducto().getId()));
        dto.setProductoNombre(stock.getLote().getProducto().getNombreComercial());
        dto.setAlmacenId(stock.getAlmacen().getId());
        dto.setAlmacenNombre(stock.getAlmacen().getNombre());
        dto.setCantidadDisponible(stock.getCantidadDisponible());
        dto.setLoteId(Long.valueOf(stock.getLote().getId()));
        dto.setLoteCodigo(stock.getLote().getCodigoLote());

        return dto;

    }
}
