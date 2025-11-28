package com.granotec.inventory_api.inventario.Mapper;

import com.granotec.inventory_api.Stock_Almacen.StockAlmacen;
import com.granotec.inventory_api.inventario.dto.StockAlmacenResponse;
import org.springframework.stereotype.Component;

@Component
public class StockAlmacenMapper {

    public StockAlmacenResponse toDTO(StockAlmacen stock) {
        if (stock == null) return null;

    StockAlmacenResponse dto = new StockAlmacenResponse();
        dto.setStockAlmacenId(stock.getId());
        dto.setAlmacenId(stock.getAlmacen().getId());
        dto.setAlmacenNombre(stock.getAlmacen().getNombre());
        dto.setProductoId(Long.valueOf(stock.getProducto().getId()));
        dto.setProductoNombre(stock.getProducto().getNombreComercial());
        dto.setCantidad(stock.getCantidad());

        return dto;
    }


}
