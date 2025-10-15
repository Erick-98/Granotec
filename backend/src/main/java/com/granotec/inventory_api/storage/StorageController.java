package com.granotec.inventory_api.storage;

import com.granotec.inventory_api.common.dto.ApiResponse;
import com.granotec.inventory_api.storage.dto.StorageRequest;
import com.granotec.inventory_api.storage.dto.StorageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/storage")
@RequiredArgsConstructor
public class StorageController {

    private final StorageService service;

    @PostMapping
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<StorageResponse>> create(@Valid @RequestBody StorageRequest request){
        StorageResponse result = service.create(request);
        return ResponseEntity.ok(new ApiResponse<>("Almacen creado",result));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<StorageResponse>>> list() {
        return ResponseEntity.ok(new ApiResponse<>("Listado almacenes", service.listAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<StorageResponse>> get(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse<>("Almacen encontrado", service.getById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<StorageResponse>> update(@PathVariable Long id, @Valid @RequestBody StorageRequest request) {
        return ResponseEntity.ok(new ApiResponse<>("Almacen actualizado", service.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        service.softDelete(id);
        return ResponseEntity.ok(new ApiResponse<>("Almacen eliminado", null));
    }



}
