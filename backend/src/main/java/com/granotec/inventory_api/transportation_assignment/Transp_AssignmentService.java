package com.granotec.inventory_api.transportation_assignment;

import com.granotec.inventory_api.common.exception.BadRequestException;
import com.granotec.inventory_api.common.exception.ResourceNotFoundException;
import com.granotec.inventory_api.transportation_assignment.car.CarRepository;
import com.granotec.inventory_api.transportation_assignment.carrier.CarrierRepository;
import com.granotec.inventory_api.transportation_assignment.driver.DriverRepository;
import com.granotec.inventory_api.transportation_assignment.dto.TranspAssignmentRequest;
import com.granotec.inventory_api.transportation_assignment.dto.TranspAssignmentResponse;
import com.granotec.inventory_api.transportation_assignment.dto.TranspAssignmentStatsResponse;
import com.granotec.inventory_api.dispatch.DispatchRepository;
import com.granotec.inventory_api.common.enums.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class Transp_AssignmentService {

    private final Transp_AssignmentRepository repository;
    private final CarrierRepository carrierRepository;
    private final DriverRepository driverRepository;
    private final CarRepository carRepository;
    private final DispatchRepository dispatchRepository;

    public TranspAssignmentResponse create(TranspAssignmentRequest req) {
        var carrier = carrierRepository.findById(req.getCarrierId())
                .orElseThrow(() -> new BadRequestException("Transportista no encontrado"));
        var driver = driverRepository.findById(req.getDriverId())
                .orElseThrow(() -> new BadRequestException("Chofer no encontrado"));
        var car = carRepository.findById(req.getCarId())
                .orElseThrow(() -> new BadRequestException("Vehiculo no encontrado"));
        Transp_Assignment t = new Transp_Assignment();
        t.setCarrier(carrier);
        t.setDriver(driver);
        t.setCar(car);
        t = repository.save(t);
        return toDto(t);
    }

    public TranspAssignmentResponse update(Integer id, TranspAssignmentRequest req) {
        Transp_Assignment t = repository.findById(id)
                .filter(x -> !Boolean.TRUE.equals(x.getIsDeleted()))
                .orElseThrow(() -> new ResourceNotFoundException("Asignacion no encontrada"));
        var carrier = carrierRepository.findById(req.getCarrierId())
                .orElseThrow(() -> new BadRequestException("Transportista no encontrado"));
        var driver = driverRepository.findById(req.getDriverId())
                .orElseThrow(() -> new BadRequestException("Chofer no encontrado"));
        var car = carRepository.findById(req.getCarId())
                .orElseThrow(() -> new BadRequestException("Vehiculo no encontrado"));
        t.setCarrier(carrier);
        t.setDriver(driver);
        t.setCar(car);
        t = repository.save(t);
        return toDto(t);
    }

    public Page<TranspAssignmentResponse> list(Pageable pageable) {
        return repository.findAll(pageable).map(this::toDto);
    }

    public TranspAssignmentResponse getById(Integer id) {
        Transp_Assignment t = repository.findById(id)
                .filter(x -> !Boolean.TRUE.equals(x.getIsDeleted()))
                .orElseThrow(() -> new ResourceNotFoundException("Asignacion no encontrada"));
        return toDto(t);
    }

    public void softDelete(Integer id) {
        Transp_Assignment t = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Asignacion no encontrada"));
        if (Boolean.TRUE.equals(t.getIsDeleted())) {
            throw new BadRequestException("Asignacion ya eliminada");
        }
        t.softDelete();
        repository.save(t);
    }

    public TranspAssignmentResponse toDto(Transp_Assignment t) {
        return new TranspAssignmentResponse(
                t.getId(),
                t.getCarrier() != null ? t.getCarrier().getId() : null,
                t.getDriver() != null ? t.getDriver().getId() : null,
                t.getCar() != null ? t.getCar().getId() : null
        );
    }

    public Page<TranspAssignmentResponse> listByCarrier(Integer carrierId, Pageable pageable) {
        // validate carrier exists
        carrierRepository.findById(carrierId).orElseThrow(() -> new ResourceNotFoundException("Transportista no encontrado"));
        return repository.findByCarrier_IdAndIsDeletedFalse(carrierId, pageable).map(this::toDto);
    }

    public Page<TranspAssignmentResponse> listByDriver(Long driverId, Pageable pageable) {
        driverRepository.findById(driverId).orElseThrow(() -> new ResourceNotFoundException("Chofer no encontrado"));
        return repository.findByDriver_IdAndIsDeletedFalse(driverId, pageable).map(this::toDto);
    }

    public TranspAssignmentStatsResponse getStats(LocalDate from, LocalDate to) {
        long totalAssignments = repository.count();
        long totalDispatches = dispatchRepository.countByFechaBetween(from, to);
        long pending = dispatchRepository.countByEstadoAndFechaBetween(Status.PENDIENTE, from, to);
        Double avgDays = dispatchRepository.avgDeliveryDaysByFechaBetweenAndEstado(from, to, Status.ENTREGADO.name());
        return new TranspAssignmentStatsResponse(totalAssignments, totalDispatches, pending, avgDays);
    }

}
