package com.granotec.inventory_api.transportation_assignment;

import com.granotec.inventory_api.transportation_assignment.dto.TranspAssignmentRequest;
import com.granotec.inventory_api.transportation_assignment.dto.TranspAssignmentResponse;
import com.granotec.inventory_api.transportation_assignment.dto.TranspAssignmentStatsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;

@RestController
@RequestMapping("/transp_assignment")
@RequiredArgsConstructor
public class Transp_AssignmentController {

    private final Transp_AssignmentService service;

    @PostMapping
    public ResponseEntity<TranspAssignmentResponse> create(@Valid @RequestBody TranspAssignmentRequest req) {
        return ResponseEntity.ok(service.create(req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TranspAssignmentResponse> update(@PathVariable Integer id, @Valid @RequestBody TranspAssignmentRequest req) {
        return ResponseEntity.ok(service.update(id, req));
    }

    @GetMapping
    public ResponseEntity<Page<TranspAssignmentResponse>> list(Pageable pageable) {
        return ResponseEntity.ok(service.list(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TranspAssignmentResponse> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PatchMapping("/{id}/delete")
    public ResponseEntity<Void> softDelete(@PathVariable Integer id) {
        service.softDelete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats")
    public ResponseEntity<TranspAssignmentStatsResponse> stats(@RequestParam(required = false) String from,
                                                                @RequestParam(required = false) String to) {
        LocalDate f = from == null ? LocalDate.now().minusMonths(1) : LocalDate.parse(from);
        LocalDate t = to == null ? LocalDate.now() : LocalDate.parse(to);
        return ResponseEntity.ok(service.getStats(f, t));
    }
}
