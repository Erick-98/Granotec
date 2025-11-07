package com.granotec.inventory_api.movement;

import com.granotec.inventory_api.common.dto.PagedResponse;
import com.granotec.inventory_api.movement.projection.MovementListProjection;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/movements")
public class MovementController {

    private final MovementService movementService;

    public MovementController(MovementService movementService) {
        this.movementService = movementService;
    }

    @PreAuthorize("@permissionService.has('movement:view')")
    @GetMapping
    public PagedResponse<MovementListProjection> list(
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            @RequestParam(required = false) Long almacenId,
            @RequestParam(required = false) Integer productId,
            @RequestParam(required = false) OperationType tipoOperacion,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        LocalDate from = fromDate != null ? LocalDate.parse(fromDate) : null;
        LocalDate to = toDate != null ? LocalDate.parse(toDate) : null;
        return movementService.findByFilters(from, to, almacenId, productId, tipoOperacion, page, size);
    }
}
