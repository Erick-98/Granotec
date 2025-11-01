package com.granotec.inventory_api.dispatch;

import com.granotec.inventory_api.common.exception.BadRequestException;
import com.granotec.inventory_api.common.exception.ResourceNotFoundException;
import com.granotec.inventory_api.dispatch.dto.DispatchRequest;
import com.granotec.inventory_api.dispatch.dto.DispatchResponse;
import com.granotec.inventory_api.dispatch.dto.DispatchStatsResponse;
import com.granotec.inventory_api.ov.OvRepository;
import com.granotec.inventory_api.transportation_assignment.Transp_AssignmentRepository;
import com.granotec.inventory_api.transportation_assignment.Transp_Assignment;
import com.granotec.inventory_api.dispatch.details_dispatch.DetailsDispatchService;
import com.granotec.inventory_api.dispatch.details_dispatch.DetailsDispatchRepository;
import com.granotec.inventory_api.dispatch.details_dispatch.dto.DetailsDispatchResponse;
import com.granotec.inventory_api.common.enums.Status;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DispatchService {

    private final DispatchRepository repository;
    private final OvRepository ovRepository;
    private final Transp_AssignmentRepository assignmentRepository;
    private final DetailsDispatchRepository detailsDispatchRepository;
    private final DetailsDispatchService detailsDispatchService;

    public DispatchResponse create(DispatchRequest req) {
        ovRepository.findById(req.getIdOrdenVenta())
                .orElseThrow(() -> new BadRequestException("Orden de venta no encontrada"));
        Transp_Assignment ta = assignmentRepository.findById(req.getIdAsignacion())
                .orElseThrow(() -> new BadRequestException("Asignación no encontrada"));

        Dispatch d = new Dispatch();
        d.setOrdenVenta(ovRepository.findById(req.getIdOrdenVenta()).get());
        d.setAsignacion(ta);
        d.setFechaDespacho(req.getFechaDespacho());
        d.setEstado(req.getEstado());

        d = repository.save(d);
        return toDto(d);
    }

    public DispatchResponse update(Integer id, DispatchRequest req) {
        Dispatch d = repository.findById(id)
                .filter(x -> !Boolean.TRUE.equals(x.getIsDeleted()))
                .orElseThrow(() -> new ResourceNotFoundException("Despacho no encontrado"));

        ovRepository.findById(req.getIdOrdenVenta())
                .orElseThrow(() -> new BadRequestException("Orden de venta no encontrada"));
        Transp_Assignment ta = assignmentRepository.findById(req.getIdAsignacion())
                .orElseThrow(() -> new BadRequestException("Asignación no encontrada"));

        d.setOrdenVenta(ovRepository.findById(req.getIdOrdenVenta()).get());
        d.setAsignacion(ta);
        d.setFechaDespacho(req.getFechaDespacho());
        d.setEstado(req.getEstado());

        d = repository.save(d);
        return toDto(d);
    }

    public Page<DispatchResponse> list(Pageable pageable) {
        return repository.findAll(pageable).map(this::toDto);
    }

    public DispatchResponse getById(Integer id) {
        Dispatch d = repository.findById(id)
                .filter(x -> !Boolean.TRUE.equals(x.getIsDeleted()))
                .orElseThrow(() -> new ResourceNotFoundException("Despacho no encontrado"));
        return toDto(d);
    }

    @Transactional
    public void softDelete(Integer id) {
        Dispatch d = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Despacho no encontrado"));
        if (Boolean.TRUE.equals(d.getIsDeleted())) {
            throw new BadRequestException("Despacho ya eliminado");
        }
        d.softDelete();
        repository.save(d);
    }

    @Transactional
    public DispatchResponse updateStatus(Integer id, Status newStatus) {
        Dispatch d = repository.findById(id)
                .filter(x -> !Boolean.TRUE.equals(x.getIsDeleted()))
                .orElseThrow(() -> new ResourceNotFoundException("Despacho no encontrado"));
        // possible business rules: cannot move from DELIVERED back to PENDING
        if (d.getEstado() == Status.ENTREGADO && newStatus != Status.ENTREGADO) {
            throw new BadRequestException("No es posible cambiar el estado de un despacho ya entregado");
        }
        d.setEstado(newStatus);
        // if delivered, set fechaDespacho = today if not set
        if (newStatus == Status.ENTREGADO && d.getFechaDespacho() == null) {
            d.setFechaDespacho(LocalDate.now());
        }
        d = repository.save(d);
        return toDto(d);
    }

    public List<DetailsDispatchResponse> listDetailsByDispatchId(Integer dispatchId) {
        // validate dispatch
        Dispatch d = repository.findById(dispatchId)
                .filter(x -> !Boolean.TRUE.equals(x.getIsDeleted()))
                .orElseThrow(() -> new ResourceNotFoundException("Despacho no encontrado"));
        return detailsDispatchRepository.findAll()
                .stream()
                .filter(dd -> dd.getDispatch() != null && dd.getDispatch().getId().equals(dispatchId))
                .filter(dd -> !Boolean.TRUE.equals(dd.getIsDeleted()))
                .map(detailsDispatchService::toDto)
                .collect(Collectors.toList());
    }

    public DispatchStatsResponse getStats(LocalDate from, LocalDate to) {
        long total = repository.countByFechaBetween(from, to);
        long pending = repository.countByEstadoAndFechaBetween(Status.PENDIENTE, from, to);
        Double avgDays = repository.avgDeliveryDaysByFechaBetweenAndEstado(from, to, Status.ENTREGADO.name());
        return new DispatchStatsResponse(total, pending, avgDays == null ? null : avgDays);
    }

    private DispatchResponse toDto(Dispatch d) {
        return new DispatchResponse(
                d.getId(),
                d.getOrdenVenta() != null ? d.getOrdenVenta().getId() : null,
                d.getAsignacion() != null ? d.getAsignacion().getId() : null,
                d.getFechaDespacho(),
                d.getEstado() != null ? d.getEstado().name() : null
        );
    }

}
