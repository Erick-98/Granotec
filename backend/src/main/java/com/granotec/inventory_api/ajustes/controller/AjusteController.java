package com.granotec.inventory_api.ajustes.controller;

import com.granotec.inventory_api.ajustes.dto.AjusteRequest;
import com.granotec.inventory_api.ajustes.dto.AjusteResponse;
import com.granotec.inventory_api.ajustes.service.AjusteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/ajustes")
public class AjusteController {
    @Autowired
    private AjusteService ajusteService;

    @PostMapping
    public ResponseEntity<AjusteResponse> registrarAjuste(@Valid @RequestBody AjusteRequest request) {
        return ResponseEntity.ok(ajusteService.registrarAjuste(request));
    }

    @GetMapping
    public ResponseEntity<List<AjusteResponse>> listarAjustes() {
        return ResponseEntity.ok(ajusteService.listarAjustes());
    }
}
