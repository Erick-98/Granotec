package com.granotec.inventory_api.ventas.controller;

import com.granotec.inventory_api.ventas.dto.VentaRequest;
import com.granotec.inventory_api.ventas.dto.VentaResponse;
import com.granotec.inventory_api.ventas.service.VentaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/ventas")
public class VentaController {
    @Autowired
    private VentaService ventaService;

    @PostMapping
    public ResponseEntity<VentaResponse> crearVenta(@Valid @RequestBody VentaRequest request) {
        return ResponseEntity.ok(ventaService.crearVenta(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VentaResponse> obtenerVenta(@PathVariable Integer id) {
        return ResponseEntity.ok(ventaService.obtenerVenta(id));
    }

    @GetMapping
    public ResponseEntity<List<VentaResponse>> listarVentas() {
        return ResponseEntity.ok(ventaService.listarVentas());
    }
}
