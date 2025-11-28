package com.granotec.inventory_api.inventario.service;

import com.granotec.inventory_api.inventario.dto.*;
import java.util.List;

public interface InventarioService {
    MovimientoInventarioResponse registrarEntrada(MovimientoInventarioRequest request);
    MovimientoInventarioResponse registrarSalida(MovimientoInventarioRequest request);
    MovimientoInventarioResponse registrarAjuste(AjusteInventarioRequest request);
    List<StockAlmacenResponse> obtenerStockPorAlmacen(Long almacenId);
    List<StockLoteResponse> obtenerStockPorLote(Integer productoId, Long almacenId);
    List<StockDisponibleResponse> obtenerStockDisponible(Integer productoId);
    MovimientoInventarioResponse transferir(TransferenciaRequest request);
}

