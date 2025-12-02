package com.granotec.inventory_api.ajustes.service;

import com.granotec.inventory_api.ajustes.dto.*;
import java.util.List;

public interface AjusteService {
    AjusteResponse registrarAjuste(AjusteRequest request);
    List<AjusteResponse> listarAjustes();
}

