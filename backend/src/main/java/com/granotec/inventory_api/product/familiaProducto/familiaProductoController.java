package com.granotec.inventory_api.product.familiaProducto;

import com.granotec.inventory_api.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import com.granotec.inventory_api.product.familiaProducto.dto.familyProductResponse;
import com.granotec.inventory_api.product.familiaProducto.dto.familyProductRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/family_product")
@RequiredArgsConstructor
public class familiaProductoController {

    private final familiaProductoService service;

    @PostMapping
    public ResponseEntity<ApiResponse<familyProductResponse>> create(@Valid @RequestBody familyProductRequest request){
        familyProductResponse result = service.create(request);
        return ResponseEntity.ok(new ApiResponse<>("Familia de producto creada correctamente",result));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<familyProductResponse>>> list() {
        return ResponseEntity.ok(new ApiResponse<>("Listado de familias de productos", service.listAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<familyProductResponse>> get(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse<>("Familia de producto encontrada", service.getById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<familyProductResponse>> update(@PathVariable Long id, @Valid @RequestBody familyProductRequest request) {
        return ResponseEntity.ok(new ApiResponse<>("Familia de producto actualizada", service.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        service.softDelete(id);
        return ResponseEntity.ok(new ApiResponse<>("Familia de producto eliminada", null));
    }

}
