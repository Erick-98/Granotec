package com.granotec.inventory_api.produccion.controller;

import com.granotec.inventory_api.produccion.dto.*;
import com.granotec.inventory_api.produccion.service.ProduccionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/produccion1")
public class ProduccionController {
    @Autowired
    private ProduccionService produccionService;

    @PostMapping("/orden")
    public ResponseEntity<OrdenProduccionResponse> crearOrden(@Valid @RequestBody OrdenProduccionRequest request) {
        return ResponseEntity.ok(produccionService.crearOrden(request));
    }

    @PostMapping("/{id}/iniciar")
    public ResponseEntity<OrdenProduccionResponse> iniciarOrden(@PathVariable Integer id) {
        return ResponseEntity.ok(produccionService.iniciarOrden(id));
    }

    @PostMapping("/{id}/consumos")
    public ResponseEntity<OrdenProduccionResponse> registrarConsumo(@PathVariable Integer id, @Valid @RequestBody ConsumoInsumoRequest request) {
        request.setOrdenProduccionId(id);
        return ResponseEntity.ok(produccionService.registrarConsumo(request));
    }

    @PostMapping("/{id}/finalizar")
    public ResponseEntity<OrdenProduccionResponse> finalizarOrden(@PathVariable Integer id, @Valid @RequestBody FinalizarProduccionRequest request) {
        request.setOrdenProduccionId(id);
        return ResponseEntity.ok(produccionService.finalizarOrden(request));
    }

    @GetMapping
    public ResponseEntity<List<OrdenProduccionResponse>> listarOrdenes() {
        return ResponseEntity.ok(produccionService.listarOrdenes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrdenProduccionResponse> obtenerOrden(@PathVariable Integer id) {
        return ResponseEntity.ok(produccionService.obtenerOrden(id));
    }
}
