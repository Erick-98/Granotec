package com.granotec.inventory_api.transportation_assignment.carrier;

import com.granotec.inventory_api.transportation_assignment.Transp_AssignmentService;
import com.granotec.inventory_api.transportation_assignment.carrier.dto.CarrierRequest;
import com.granotec.inventory_api.transportation_assignment.carrier.dto.CarrierResponse;
import com.granotec.inventory_api.transportation_assignment.dto.TranspAssignmentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/carrier")
@RequiredArgsConstructor
public class CarrierController {

    private final CarrierService service;
    private final Transp_AssignmentService assignmentService;

    @PostMapping
    public ResponseEntity<CarrierResponse> create(@Valid @RequestBody CarrierRequest req) {
        return ResponseEntity.ok(service.create(req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CarrierResponse> update(@PathVariable Integer id, @Valid @RequestBody CarrierRequest req) {
        return ResponseEntity.ok(service.update(id, req));
    }

    @GetMapping
    public ResponseEntity<Page<CarrierResponse>> list(Pageable pageable) {
        return ResponseEntity.ok(service.list(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarrierResponse> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PatchMapping("/{id}/delete")
    public ResponseEntity<Void> softDelete(@PathVariable Integer id) {
        service.softDelete(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/{id}/assignments")
    public ResponseEntity<Page<TranspAssignmentResponse>> listAssignmentsByCarrier(
            @PathVariable Integer id, Pageable pageable) {
        return ResponseEntity.ok(assignmentService.listByCarrier(id, pageable));
    }
}
