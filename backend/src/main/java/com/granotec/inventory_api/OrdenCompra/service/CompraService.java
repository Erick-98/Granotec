package com.granotec.inventory_api.OrdenCompra.service;

import com.granotec.inventory_api.OrdenCompra.dto.CompraRequest;
import com.granotec.inventory_api.OrdenCompra.dto.CompraResponse;

import java.util.List;

public interface CompraService {
    CompraResponse registrarCompra(CompraRequest request);
    CompraResponse obtenerCompra(Integer id);
    List<CompraResponse> listarCompras();
    CompraResponse actualizarCompra(Integer id, CompraRequest request);
    void eliminarCompra(Integer id);
}

