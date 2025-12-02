package com.granotec.inventory_api.salesperson;

import com.granotec.inventory_api.common.dto.ApiResponse;
import com.granotec.inventory_api.salesperson.dto.SalespersonRequest;
import com.granotec.inventory_api.salesperson.dto.SalespersonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/salesperson")
@RequiredArgsConstructor
public class SalespersonController {

    private final SalespersonService service;

    @GetMapping
    public ResponseEntity<ApiResponse<List<SalespersonResponse>>> list() {
        return ResponseEntity.ok(new ApiResponse<>("Lista de vendedores", service.listAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SalespersonResponse> get(@PathVariable Integer id){
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SalespersonResponse> create(@RequestBody SalespersonRequest req){
        return ResponseEntity.ok(service.create(req));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SalespersonResponse> update(@PathVariable Integer id, @RequestBody SalespersonRequest req){
        return ResponseEntity.ok(service.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id) {
        service.softDelete(id);
        return ResponseEntity.ok(new ApiResponse<>("Vendedor eliminado correctamente", null));
    }

//    @DeleteMapping("/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<String> delete(@PathVariable Integer id){
//        service.delete(id);
//        return ResponseEntity.ok("deleted");
//    }

//    @GetMapping("/{id}/orders")
//    public ResponseEntity<?> orders(@PathVariable Integer id, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size){
//        return ResponseEntity.ok(service.listOrders(id,page,size));
//    }
//
//    @GetMapping("/{id}/stats")
//    public ResponseEntity<Map<String, Object>> stats(@PathVariable Integer id){
//        return ResponseEntity.ok(service.statsForSalesperson(id));
//    }
}

