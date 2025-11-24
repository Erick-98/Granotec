package com.granotec.inventory_api.produccion.service;

import com.granotec.inventory_api.produccion.dto.*;
import java.util.List;

public interface ProduccionService {
    OrdenProduccionResponse crearOrden(OrdenProduccionRequest request);
    OrdenProduccionResponse iniciarOrden(Integer ordenId);
    OrdenProduccionResponse registrarConsumo(ConsumoInsumoRequest request);
    OrdenProduccionResponse finalizarOrden(FinalizarProduccionRequest request);
    List<OrdenProduccionResponse> listarOrdenes();
    OrdenProduccionResponse obtenerOrden(Integer ordenId);
}

