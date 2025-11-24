package com.granotec.inventory_api.compras.service;

import com.granotec.inventory_api.compras.dto.*;
import java.util.List;

public interface CompraService {
    CompraResponse registrarCompra(CompraRequest request);
    CompraResponse obtenerCompra(Integer id);
    List<CompraResponse> listarCompras();
}

