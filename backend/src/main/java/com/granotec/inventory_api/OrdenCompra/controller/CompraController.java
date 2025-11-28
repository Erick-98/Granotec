package com.granotec.inventory_api.OrdenCompra.controller;

import com.granotec.inventory_api.OrdenCompra.dto.CompraRequest;
import com.granotec.inventory_api.OrdenCompra.dto.CompraResponse;
import com.granotec.inventory_api.OrdenCompra.service.CompraService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/compras")
public class CompraController {
    @Autowired
    private CompraService compraService;

    @PostMapping
    public ResponseEntity<CompraResponse> registrarCompra(@Valid @RequestBody CompraRequest request) {
        return ResponseEntity.ok(compraService.registrarCompra(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompraResponse> obtenerCompra(@PathVariable Integer id) {
        return ResponseEntity.ok(compraService.obtenerCompra(id));
    }

    @GetMapping
    public ResponseEntity<List<CompraResponse>> listarCompras() {
        return ResponseEntity.ok(compraService.listarCompras());
    }
}
