package com.granotec.inventory_api.ventas.service;

import com.granotec.inventory_api.ventas.dto.*;
import java.util.List;

public interface VentaService {
    VentaResponse crearVenta(VentaRequest request);
    VentaResponse obtenerVenta(Integer id);
    List<VentaResponse> listarVentas();
}

