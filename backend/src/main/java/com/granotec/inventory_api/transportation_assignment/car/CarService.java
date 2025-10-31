package com.granotec.inventory_api.transportation_assignment.car;

import com.granotec.inventory_api.common.exception.BadRequestException;
import com.granotec.inventory_api.common.exception.ResourceNotFoundException;
import com.granotec.inventory_api.transportation_assignment.car.dto.CarRequest;
import com.granotec.inventory_api.transportation_assignment.car.dto.CarResponse;
import com.granotec.inventory_api.transportation_assignment.carrier.Carrier;
import com.granotec.inventory_api.transportation_assignment.carrier.CarrierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CarService {

    private final CarRepository repository;
    private final CarrierRepository carrierRepository;

    public CarResponse create(CarRequest req) {
        if (req.getPlaca() == null || req.getPlaca().isBlank()) {
            throw new BadRequestException("Placa requerida");
        }
        if (repository.existsByPlaca(req.getPlaca())) {
            throw new BadRequestException("Placa ya registrada");
        }
        Carrier carrier = carrierRepository.findById(req.getCarrierId())
                .orElseThrow(() -> new BadRequestException("Transportista no encontrado"));
        Car c = new Car();
        c.setPlaca(req.getPlaca());
        c.setMarca(req.getMarca());
        c.setCarrier(carrier);
        c = repository.save(c);
        return toDto(c);
    }

    public CarResponse update(Long id, CarRequest req) {
        Car c = repository.findById(id)
                .filter(x -> !Boolean.TRUE.equals(x.getIsDeleted()))
                .orElseThrow(() -> new ResourceNotFoundException("Carro no encontrado"));
        if (req.getPlaca() != null && !req.getPlaca().equals(c.getPlaca())) {
            if (repository.existsByPlaca(req.getPlaca())) {
                throw new BadRequestException("Placa ya registrada");
            }
            c.setPlaca(req.getPlaca());
        }
        c.setMarca(req.getMarca());
        if (req.getCarrierId() != null) {
            Carrier carrier = carrierRepository.findById(req.getCarrierId())
                    .orElseThrow(() -> new BadRequestException("Transportista no encontrado"));
            c.setCarrier(carrier);
        }
        c = repository.save(c);
        return toDto(c);
    }

    public Page<CarResponse> list(Pageable pageable) {
        return repository.findAllByIsDeletedFalse(pageable).map(this::toDto);
    }

    public CarResponse getById(Long id) {
        Car c = repository.findById(id)
                .filter(x -> !Boolean.TRUE.equals(x.getIsDeleted()))
                .orElseThrow(() -> new ResourceNotFoundException("Carro no encontrado"));
        return toDto(c);
    }

    public void softDelete(Long id) {
        Car c = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Carro no encontrado"));
        if (Boolean.TRUE.equals(c.getIsDeleted())) {
            throw new BadRequestException("Carro ya eliminado");
        }
        c.softDelete();
        repository.save(c);
    }

    public CarResponse toDto(Car c) {
        return new CarResponse(
                c.getId(),
                c.getPlaca(),
                c.getMarca(),
                c.getCarrier() != null ? c.getCarrier().getId() : null
        );
    }
}

