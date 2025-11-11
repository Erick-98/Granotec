package com.granotec.inventory_api.Despacho;

import com.granotec.inventory_api.Despacho.dto.DespachoRequest;
import com.granotec.inventory_api.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/despacho")
@RequiredArgsConstructor
public class DespachoController {

    private final DespachoService servicio;

    @PostMapping
    @PreAuthorize("@permissionService.has('despacho:create')")
    public ResponseEntity<ApiResponse<Object>> create(@Valid @RequestBody DespachoRequest req){
        return ResponseEntity.ok(new ApiResponse<>("Despacho creado", servicio.create(req)));
    }
}

