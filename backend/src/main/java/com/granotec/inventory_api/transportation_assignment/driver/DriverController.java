package com.granotec.inventory_api.transportation_assignment.driver;

import com.granotec.inventory_api.transportation_assignment.Transp_AssignmentService;
import com.granotec.inventory_api.transportation_assignment.driver.dto.DriverRequest;
import com.granotec.inventory_api.transportation_assignment.driver.dto.DriverResponse;
import com.granotec.inventory_api.transportation_assignment.dto.TranspAssignmentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/driver")
@RequiredArgsConstructor
public class DriverController {

    private final DriverService service;
    private final Transp_AssignmentService assignmentService;

    @PostMapping
    public ResponseEntity<DriverResponse> create(@Valid @RequestBody DriverRequest req) {
        return ResponseEntity.ok(service.create(req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DriverResponse> update(@PathVariable Long id, @Valid @RequestBody DriverRequest req) {
        return ResponseEntity.ok(service.update(id, req));
    }

    @GetMapping
    public ResponseEntity<Page<DriverResponse>> list(Pageable pageable) {
        return ResponseEntity.ok(service.list(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DriverResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PatchMapping("/{id}/delete")
    public ResponseEntity<Void> softDelete(@PathVariable Long id) {
        service.softDelete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/assignments")
    public ResponseEntity<Page<TranspAssignmentResponse>> listAssignmentsByDriver(@PathVariable Long id, Pageable pageable) {
        return ResponseEntity.ok(assignmentService.listByDriver(id,pageable));
    }


}
