package com.granotec.inventory_api.transportation_assignment.carrier;

import com.granotec.inventory_api.common.exception.BadRequestException;
import com.granotec.inventory_api.common.exception.ResourceNotFoundException;
import com.granotec.inventory_api.transportation_assignment.carrier.dto.CarrierRequest;
import com.granotec.inventory_api.transportation_assignment.carrier.dto.CarrierResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CarrierService {

    private final CarrierRepository repository;

    public CarrierResponse create(CarrierRequest req) {
        Carrier c = new Carrier();
        c.setRazonSocial(req.getRazonSocial());
        c.setTipoDocumento(req.getTipoDocumento());
        c.setNroDocumento(req.getDocumentNumber());
        c.setEmail(req.getEmail());
        c.setTelefono(req.getPhone());
        c = repository.save(c);
        return toDto(c);
    }

    public CarrierResponse update(Integer id, CarrierRequest req) {
        Carrier c = repository.findById(id)
                .filter(x -> !Boolean.TRUE.equals(x.getIsDeleted()))
                .orElseThrow(() -> new ResourceNotFoundException("Transportista no encontrado"));
        c.setRazonSocial(req.getRazonSocial());
        c.setTipoDocumento(req.getTipoDocumento());
        c.setNroDocumento(req.getDocumentNumber());
        c.setEmail(req.getEmail());
        c.setTelefono(req.getPhone());
        c = repository.save(c);
        return toDto(c);
    }

    public Page<CarrierResponse> list(Pageable pageable) {
        return repository.findAllByIsDeletedFalse(pageable).map(this::toDto);
    }

    public CarrierResponse getById(Integer id) {
        Carrier c = repository.findById(id)
                .filter(x -> !Boolean.TRUE.equals(x.getIsDeleted()))
                .orElseThrow(() -> new ResourceNotFoundException("Transportista no encontrado"));
        return toDto(c);
    }

    public void softDelete(Integer id) {
        Carrier c = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transportista no encontrado"));
        if (Boolean.TRUE.equals(c.getIsDeleted())) {
            throw new BadRequestException("Transportista ya eliminado");
        }
        c.softDelete();
        repository.save(c);
    }

    public CarrierResponse toDto(Carrier c) {
        return new CarrierResponse(
                c.getId(),
                c.getRazonSocial(),
                c.getTipoDocumento().name(),
                c.getNroDocumento(),
                c.getEmail(),
                c.getTelefono()
        );
    }
}
