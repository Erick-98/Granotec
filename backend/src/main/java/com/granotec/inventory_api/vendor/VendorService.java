package com.granotec.inventory_api.vendor;

import com.granotec.inventory_api.common.exception.BadRequestException;
import com.granotec.inventory_api.common.exception.ResourceNotFoundException;
import com.granotec.inventory_api.common.enums.DocumentType;
import com.granotec.inventory_api.vendor.dto.VendorRequest;
import com.granotec.inventory_api.vendor.dto.VendorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VendorService {

    private final VendorRepository repository;

    @Transactional
    public VendorResponse create(VendorRequest request){
        validateDocumento(request.getTipoDocumento(),request.getDocumento());
        if(request.getEmail() != null && repository.findByEmail(request.getEmail()).isPresent()){
            throw new BadRequestException("El correo electrónico ya está en uso");
        }
        if(request.getDocumento() != null && repository.findByNroDocumento(request.getDocumento()).isPresent()){
            throw new BadRequestException("El documento ya está en uso");
        }

        // Creamos la entidad asignando los campos heredados desde Person mediante setters
        Vendor v = new Vendor();
        v.setRazonSocial(request.getNombre());
        v.setTipoDocumento(request.getTipoDocumento());
        v.setNroDocumento(request.getDocumento());
        v.setDireccion(request.getDireccion());
        v.setTelefono(request.getTelefono());
        v.setEmail(request.getEmail());
        v.setNotas(request.getNotas());
        v.setMoneda(request.getMoneda());
        v.setCondicionPago(request.getCondicionPago());

        v = repository.save(v);
        return toDto(v);
    }

    @Transactional
    public VendorResponse update(Long id, VendorRequest request) {
        Vendor v = repository.findById(id)
                .filter(pr -> !Boolean.TRUE.equals(pr.getIsDeleted()))
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado"));

        validateDocumento(request.getTipoDocumento(), request.getDocumento());

        if (request.getEmail() != null && repository.findByEmail(request.getEmail()).filter(x -> !x.getId().equals(id)).isPresent()) {
            throw new BadRequestException("El correo electrónico ya está en uso");
        }
        if (request.getDocumento() != null && repository.findByNroDocumento(request.getDocumento()).filter(x -> !x.getId().equals(id)).isPresent()) {
            throw new BadRequestException("El documento ya está en uso.");
        }

        v.setRazonSocial(request.getNombre());
        v.setTipoDocumento(request.getTipoDocumento());
        v.setNroDocumento(request.getDocumento());
        v.setDireccion(request.getDireccion());
        v.setTelefono(request.getTelefono());
        v.setEmail(request.getEmail());
        v.setNotas(request.getNotas());
        v.setMoneda(request.getMoneda());
        v.setCondicionPago(request.getCondicionPago());
        v = repository.save(v);
        return toDto(v);
    }

    public List<VendorResponse> listAll() {
        return repository.findAll()
                .stream()
                .filter(p -> !Boolean.TRUE.equals(p.getIsDeleted()))
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public VendorResponse getById(Long id) {
        Vendor v = repository.findById(id)
                .filter(pr -> !Boolean.TRUE.equals(pr.getIsDeleted()))
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado"));
        return toDto(v);
    }

    @Transactional
    public void softDelete(Long id) {
        Vendor v = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado"));
        if (Boolean.TRUE.equals(v.getIsDeleted())) {
            throw new BadRequestException("El proveedor ya ha sido eliminado");
        }
        v.softDelete();
        repository.save(v);
    }

    private void validateDocumento(DocumentType tipo, String documento){
        if(tipo == DocumentType.DNI && (documento == null || documento.length() != 8)){
            throw new BadRequestException("El DNI debe tener 8 caracteres");
        }
        if(tipo == DocumentType.RUC && (documento == null || documento.length() != 11)){
            throw new BadRequestException("El RUC debe tener 11 caracteres");
        }
    }

    private VendorResponse toDto(Vendor v) {
        return new VendorResponse(
                v.getId(),
                v.getRazonSocial(),
                v.getTipoDocumento() != null ? v.getTipoDocumento().name() : null,
                v.getNroDocumento(),
                v.getDireccion(),
                v.getTelefono(),
                v.getEmail(),
                v.getNotas(),
                v.getMoneda() != null ? v.getMoneda().name() : null,
                v.getCondicionPago() != null ? v.getCondicionPago().name() : null
        );
    }






}
