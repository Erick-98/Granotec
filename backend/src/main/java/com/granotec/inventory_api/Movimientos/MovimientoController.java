package com.granotec.inventory_api.Movimientos;

import com.granotec.inventory_api.Movimientos.dto.MovimientoRequest;
import com.granotec.inventory_api.Movimientos.dto.MovimientoResponse;
import com.granotec.inventory_api.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/movimientos")
@RequiredArgsConstructor
public class MovimientoController {

    private final MovimientoService movimientoService;

    @PostMapping
    @PreAuthorize("@permissionService.has('movimiento:create')")
    public ResponseEntity<ApiResponse<MovimientoResponse>> create(@Valid @RequestBody MovimientoRequest req){
        return ResponseEntity.ok(new ApiResponse<>("Movimiento creado", movimientoService.create(req)));
    }

    @GetMapping
    @PreAuthorize("@permissionService.has('movimiento:read')")
    public ResponseEntity<ApiResponse<Page<MovimientoResponse>>> list(@RequestParam(defaultValue = "0") int page,
                                                                       @RequestParam(defaultValue = "20") int size){
        return ResponseEntity.ok(new ApiResponse<>("Listado movimientos", movimientoService.listAll(page,size)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@permissionService.has('movimiento:read')")
    public ResponseEntity<ApiResponse<MovimientoResponse>> get(@PathVariable Integer id){
        return ResponseEntity.ok(new ApiResponse<>("Movimiento encontrado", movimientoService.getById(id)));
    }

}

