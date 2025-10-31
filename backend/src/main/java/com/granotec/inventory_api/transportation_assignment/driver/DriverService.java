package com.granotec.inventory_api.transportation_assignment.driver;

import com.granotec.inventory_api.common.exception.BadRequestException;
import com.granotec.inventory_api.common.exception.ResourceNotFoundException;
import com.granotec.inventory_api.transportation_assignment.carrier.Carrier;
import com.granotec.inventory_api.transportation_assignment.carrier.CarrierRepository;
import com.granotec.inventory_api.transportation_assignment.driver.dto.DriverRequest;
import com.granotec.inventory_api.transportation_assignment.driver.dto.DriverResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DriverService {

    private final DriverRepository repository;
    private final CarrierRepository carrierRepository;

    public DriverResponse create(DriverRequest req) {
        Carrier carrier = carrierRepository.findById(req.getCarrierId())
                .orElseThrow(() -> new BadRequestException("Transportista no encontrado"));
        Driver d = new Driver();
        d.setName(req.getName());
        d.setApellidos(req.getApellidos());
        d.setTipoDocumento(req.getTipoDocumento());
        d.setNroDocumento(req.getNroDocumento());
        d.setCarrier(carrier);
        d = repository.save(d);
        return toDto(d);
    }

    public DriverResponse update(Long id, DriverRequest req) {
        Driver d = repository.findById(id)
                .filter(x -> !Boolean.TRUE.equals(x.getIsDeleted()))
                .orElseThrow(() -> new ResourceNotFoundException("Chofer no encontrado"));
        Carrier carrier = carrierRepository.findById(req.getCarrierId())
                .orElseThrow(() -> new BadRequestException("Transportista no encontrado"));
        d.setName(req.getName());
        d.setApellidos(req.getApellidos());
        d.setTipoDocumento(req.getTipoDocumento());
        d.setNroDocumento(req.getNroDocumento());
        d.setCarrier(carrier);
        d = repository.save(d);
        return toDto(d);
    }

    public Page<DriverResponse> list(Pageable pageable) {
        return repository.findAllByIsDeletedFalse(pageable).map(this::toDto);
    }

    public DriverResponse getById(Long id) {
        Driver d = repository.findById(id)
                .filter(x -> !Boolean.TRUE.equals(x.getIsDeleted()))
                .orElseThrow(() -> new ResourceNotFoundException("Chofer no encontrado"));
        return toDto(d);
    }

    public void softDelete(Long id) {
        Driver d = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Chofer no encontrado"));
        if (Boolean.TRUE.equals(d.getIsDeleted())) {
            throw new BadRequestException("Chofer ya eliminado");
        }
        d.softDelete();
        repository.save(d);
    }

    public DriverResponse toDto(Driver d) {
        return new DriverResponse(
                d.getId(),
                d.getName(),
                d.getApellidos(),
                d.getCarrier() != null ? d.getCarrier().getId() : null
        );
    }
}

