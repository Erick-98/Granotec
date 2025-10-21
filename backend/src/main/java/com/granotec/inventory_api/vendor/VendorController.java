package com.granotec.inventory_api.vendor;

import com.granotec.inventory_api.common.dto.ApiResponse;
import com.granotec.inventory_api.vendor.dto.VendorRequest;
import com.granotec.inventory_api.vendor.dto.VendorResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vendor")
@RequiredArgsConstructor
public class VendorController {

    private final VendorService service;

    @PostMapping
    public ResponseEntity<ApiResponse<VendorResponse>> create(@Valid @RequestBody VendorRequest dto) {
        return ResponseEntity.ok(new ApiResponse<>("Proveedor creado exitosamente", service.create(dto)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<VendorResponse>>> list() {
        return ResponseEntity.ok(new ApiResponse<>("Listado de proveedores", service.listAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VendorResponse>> get(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse<>("Proveedor encontrado", service.getById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<VendorResponse>> update(@PathVariable Long id, @Valid @RequestBody VendorRequest dto) {
        return ResponseEntity.ok(new ApiResponse<>("Proveedor actualizado correctamente", service.update(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        service.softDelete(id);
        return ResponseEntity.ok(new ApiResponse<>("Proveedor eliminado", null));
    }









}
