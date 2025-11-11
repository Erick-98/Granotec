package com.granotec.inventory_api.Recojo;

import com.granotec.inventory_api.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/recojo")
@RequiredArgsConstructor
public class RecojoController {

    private final RecojoService service;

    @PostMapping
    @PreAuthorize("@permissionService.has('recojo:create')")
    public ResponseEntity<ApiResponse<Recojo>> create(@Valid @RequestBody Recojo req){
        return ResponseEntity.ok(new ApiResponse<>("Recojo creado", service.create(req)));
    }
}

