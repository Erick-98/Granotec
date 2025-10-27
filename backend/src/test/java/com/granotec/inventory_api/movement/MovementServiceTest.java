package com.granotec.inventory_api.movement;

import com.granotec.inventory_api.common.dto.PagedResponse;
import com.granotec.inventory_api.movement.projection.MovementListProjection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

class MovementServiceTest {

    private MovementRepository movementRepository;
    private MovementService movementService;

    @BeforeEach
    void setUp() {
        movementRepository = Mockito.mock(MovementRepository.class);
        movementService = new MovementService(movementRepository);
    }

    @Test
    void findByFilters_returnsPagedProjection() {
        MovementListProjection proj = new MovementListProjection() {
            public Long getId() { return 1L; }
            public LocalDate getFechaMovimiento() { return LocalDate.now(); }
            public String getNumeroFactura() { return "F001"; }
            public MovementKind getTipoMovimiento() { return MovementKind.ENTRADA; }
            public OperationType getTipoOperacion() { return OperationType.COMPRA; }
            public com.granotec.inventory_api.common.enums.Status getEstado() { return com.granotec.inventory_api.common.enums.Status.ACTIVO; }
            public Long getAlmacenOrigenId() { return null; }
            public Long getAlmacenDestinoId() { return 2L; }
            public Integer getProductId() { return 10; }
            public String getProductName() { return "Prod"; }
            public String getLote() { return "L1"; }
            public BigDecimal getCantidad() { return BigDecimal.TEN; }
            public BigDecimal getTotal() { return BigDecimal.valueOf(100); }
        };

        Page<MovementListProjection> page = new PageImpl<>(List.of(proj));
        when(movementRepository.findByFilters(any(), any(), any(), any(), any(), any(Pageable.class))).thenReturn(page);

        PagedResponse<MovementListProjection> resp = movementService.findByFilters(null, null, null, null, null, 0, 10);

        assertNotNull(resp);
        assertEquals(1, resp.content.size());
        assertEquals(1L, resp.content.get(0).getId());
    }
}

