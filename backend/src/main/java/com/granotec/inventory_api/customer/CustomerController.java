package com.granotec.inventory_api.customer;

import com.granotec.inventory_api.common.dto.ApiResponse;
import com.granotec.inventory_api.customer.dto.CustomerRequest;
import com.granotec.inventory_api.customer.dto.CustomerResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService service;

    @PostMapping
    public ResponseEntity<ApiResponse<CustomerResponse>> create(@Valid @RequestBody CustomerRequest dto) {
        return ResponseEntity.ok(new ApiResponse<>("Cliente creado exitosamente", service.create(dto)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CustomerResponse>>> list() {
        return ResponseEntity.ok(new ApiResponse<>("Listado de clientes", service.listAll()));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<CustomerResponse>>> search(
            @PageableDefault(size = 20) Pageable pageable,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String documento,
            @RequestParam(required = false) String email
    ) {
        return ResponseEntity.ok(new ApiResponse<>("Clientes paginados", service.list(pageable, nombre, documento, email)));
    }

//    @GetMapping("/{id}/stats")
//    public ResponseEntity<ApiResponse<CustomerStatsResponse>> getCustomerStats(@PathVariable Long id) {
//        return ResponseEntity.ok(new ApiResponse<>("Estadísticas del cliente", service.getCustomerStats(id)));
//    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CustomerResponse>> get(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse<>("Cliente encontrado", service.getById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CustomerResponse>> update(@PathVariable Long id, @Valid @RequestBody CustomerRequest dto) {
        return ResponseEntity.ok(new ApiResponse<>("Cliente actualizado correctamente", service.update(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        service.softDelete(id);
        return ResponseEntity.ok(new ApiResponse<>("Cliente eliminado", null));
    }

}
