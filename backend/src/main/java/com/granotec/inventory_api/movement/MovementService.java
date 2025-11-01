package com.granotec.inventory_api.movement;

import com.granotec.inventory_api.common.dto.PagedResponse;
import com.granotec.inventory_api.movement.projection.MovementListProjection;
import com.granotec.inventory_api.movement.dto.MovementDetailResponse;
import com.granotec.inventory_api.movement.dto.MovementResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MovementService {

    private final MovementRepository movementRepository;

    public MovementService(MovementRepository movementRepository) {
        this.movementRepository = movementRepository;
    }

    public List<MovementResponse> findAll() {
        return movementRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    public PagedResponse<MovementListProjection> findByFilters(java.time.LocalDate fromDate,
                                                               java.time.LocalDate toDate,
                                                               Long almacenId,
                                                               Integer productId,
                                                               OperationType tipoOperacion,
                                                               int page,
                                                               int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<MovementListProjection> p = movementRepository.findByFilters(fromDate, toDate, almacenId, productId, tipoOperacion, pageable);
        PagedResponse<MovementListProjection> resp = new PagedResponse<>();
        resp.content = p.getContent();
        resp.page = p.getNumber();
        resp.size = p.getSize();
        resp.totalElements = p.getTotalElements();
        resp.totalPages = p.getTotalPages();
        return resp;
    }

    private MovementResponse toDto(Movement m) {
        MovementResponse r = new MovementResponse();
        r.id = m.getId();
        r.fechaMovimiento = m.getFechaMovimiento();
        r.almacenOrigenId = m.getAlmacenOrigen() != null ? m.getAlmacenOrigen().getId() : null;
        r.almacenDestinoId = m.getAlmacenDestino() != null ? m.getAlmacenDestino().getId() : null;
        r.tipoMovimiento = m.getTipoMovimiento();
        r.tipoOperacion = m.getTipoOperacion();
        r.numeroFactura = m.getNumeroFactura();
        r.observacion = m.getObservacion();
        r.total = m.getTotal();
        r.estado = m.getEstado();
        r.detalles = m.getDetalles() != null ? m.getDetalles().stream().map(d -> {
            MovementDetailResponse dr = new MovementDetailResponse();
            dr.id = d.getId();
            dr.movementId = m.getId();
            dr.productId = d.getProduct() != null ? d.getProduct().getId() : null; // Product.id es Integer
            dr.nombreComercial = d.getNombreComercial();
            dr.codigo = d.getCodigo();
            dr.lote = d.getLote();
            dr.ordenProduccion = d.getOrdenProduccion();
            dr.fechaIngreso = d.getFechaIngreso();
            dr.fechaProduccion = d.getFechaProduccion();
            dr.fechaVencimiento = d.getFechaVencimiento();
            dr.presentacion = d.getPresentacion();
            dr.proveedorId = d.getProveedor() != null ? d.getProveedor().getId() : null;
            dr.clienteDestinoId = d.getClienteDestino() != null ? d.getClienteDestino().getId() : null;
            dr.cantidad = d.getCantidad();
            dr.total = d.getTotal();
            return dr;
        }).collect(Collectors.toList()) : List.of();
        return r;
    }
}
