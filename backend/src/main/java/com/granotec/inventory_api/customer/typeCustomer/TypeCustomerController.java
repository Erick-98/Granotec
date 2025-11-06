package com.granotec.inventory_api.customer.typeCustomer;

import com.granotec.inventory_api.common.dto.ApiResponse;
import com.granotec.inventory_api.customer.typeCustomer.dto.TypeCustomerRequest;
import com.granotec.inventory_api.customer.typeCustomer.dto.TypeCustomerResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/type-customer")
@RequiredArgsConstructor
public class TypeCustomerController {
    
    private final TypeCustomerService service;

    @PostMapping
    public ResponseEntity<ApiResponse<TypeCustomerResponse>> create(@Valid @RequestBody TypeCustomerRequest request){
        TypeCustomerResponse result = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("Tipo de cliente creado correctamente",result));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TypeCustomerResponse>>> list() {
        return ResponseEntity.ok(new ApiResponse<>("Listado tipo de clientes", service.listAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TypeCustomerResponse>> get(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse<>("Tipo de cliente encontrado", service.getById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TypeCustomerResponse>> update(@PathVariable Long id, @Valid @RequestBody TypeCustomerRequest request) {
        return ResponseEntity.ok(new ApiResponse<>("Tipo de cliente actualizado", service.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        service.softDelete(id);
        return ResponseEntity.ok(new ApiResponse<>("Tipo de cliente eliminado", null));
    }

}
