package com.granotec.inventory_api.OrdenProduccion;

import com.granotec.inventory_api.ConsumoProduccion.ConsumoProduccion;
import com.granotec.inventory_api.OrdenProduccion.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/produccion")
@RequiredArgsConstructor
public class OrdenProduccionController {

    private final OrdenProduccionService op;

    @PostMapping("/ordenes")
    public ResponseEntity<OrdenProduccionResponse> crearOrden(@RequestBody CrearOrdenProduccionRequest request){
        return ResponseEntity.ok(op.crearOrdenProduccion(request));
    }

    @PostMapping("/ordenes/{id}/iniciar")
    public ResponseEntity<OrdenProduccionResponse> iniciarOrden(@PathVariable Integer id, @RequestBody(required = false) IniciarOrdenRequest req) {
        if (req == null) req = new IniciarOrdenRequest();
        req.setOrdenId(id);
        return ResponseEntity.ok(op.iniciarOrdenProduccion(req));
    }

    @PostMapping("/ordenes/{id}/consumos")
    public ResponseEntity<ConsumoProduccion> registrarConsumo(@PathVariable Integer id, @RequestBody RegistrarConsumoRequest req) {
        req.setOrdenId(id);
        return ResponseEntity.ok(op.registrarConsumo(req));
    }

    @PostMapping("/ordenes/{id}/laboratorio")
    public ResponseEntity<OrdenProduccionResponse> aprobarLaboratorio(@PathVariable Integer id, @RequestBody AprobarLaboratorioRequest req) {
        req.setOrdenId(id);
        return ResponseEntity.ok(op.aprobarCalidadLaboratorio(req));
    }

    @PostMapping("/ordenes/{id}/cerrar")
    public ResponseEntity<OrdenProduccionResponse> cerrarOrden(@PathVariable Integer id, @RequestBody CerrarOrdenRequest req) {
        req.setOrdenId(id);
        return ResponseEntity.ok(op.cerrarOrden(req));
    }

    @GetMapping("/ordenes/{id}")
    public ResponseEntity<OrdenProduccionResponse> obtenerOrden(@PathVariable Integer id) {
        return ResponseEntity.ok(op.obtenerOrden(id));
    }

    @GetMapping("/ordenes")
    public ResponseEntity<List<OrdenProduccionResponse>> listarOrdenes() {
        return ResponseEntity.ok(op.listarTodos());
    }


}
