package com.granotec.inventory_api.OrdenProduccion.ListaMaterial;

import com.granotec.inventory_api.OrdenProduccion.ListaMaterial.dto.ActualizarListaMaterialRequest;
import com.granotec.inventory_api.OrdenProduccion.ListaMaterial.dto.CrearListaMaterialRequest;
import com.granotec.inventory_api.OrdenProduccion.ListaMaterial.dto.ListaMaterialResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/lista-material")
@RequiredArgsConstructor
public class ListaMaterialController {

    private final ListaMaterialService listaService;

    @PostMapping
    public ResponseEntity<ListaMaterialResponse> crear(@RequestBody CrearListaMaterialRequest request) {
        return ResponseEntity.ok(listaService.crearLista(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ListaMaterialResponse> obtener(@PathVariable Integer id) {
        return ResponseEntity.ok(listaService.obtenerPorId(id));
    }

    @GetMapping("/producto/{productoId}")
    public ResponseEntity<ListaMaterialResponse> obtenerPorProducto(@PathVariable Integer productoId) {
        return ResponseEntity.ok(listaService.obtenerPorProducto(productoId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ListaMaterialResponse> actualizar(@PathVariable Integer id, @RequestBody ActualizarListaMaterialRequest req) {
        return ResponseEntity.ok(listaService.actualizar(id, req));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        listaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/requerimiento")
    public ResponseEntity<Map<Integer, BigDecimal>> requerimiento(@PathVariable Integer id,
                                                                  @RequestParam BigDecimal cantidadProducto) {
        return ResponseEntity.ok(listaService.calcularRequerimiento(id, cantidadProducto));
    }
}
