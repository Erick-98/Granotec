package com.granotec.inventory_api.salesperson;

import com.granotec.inventory_api.common.exception.BadRequestException;
import com.granotec.inventory_api.common.exception.ResourceNotFoundException;
import com.granotec.inventory_api.location.entity.District;
import com.granotec.inventory_api.location.repository.DistrictRepository;
import com.granotec.inventory_api.salesperson.dto.SalespersonRequest;
import com.granotec.inventory_api.salesperson.dto.SalespersonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class SalespersonService {

    private final SalespersonRepository repository;
    private final DistrictRepository disRepository;

    @Transactional
    public SalespersonResponse create(SalespersonRequest request){
        if(request.getNroDocumento() == null || request.getNroDocumento().isBlank()){
            throw new BadRequestException("El número de documento es obligatorio");
        }
        if(request.getEmail() != null && repository.findByEmail(request.getEmail()).isPresent()){
            throw new BadRequestException("El correo electrónico ya está en uso");
        }

        if(request.getNroDocumento() != null && repository.findByNroDocumento(request.getNroDocumento()).isPresent()){
            throw new BadRequestException("El documento ya está en uso");
        }

        District distrito = disRepository.findById(request.getDistritoId())
                .orElseThrow(() -> new ResourceNotFoundException("El distrito no existe"));

        Salesperson salesperson = new Salesperson();
        salesperson.setDistrito(distrito);

        return toDto(repository.save(salesperson));
    }

    @Transactional
    public SalespersonResponse update(Integer id, SalespersonRequest req){

        Salesperson existing = repository.findById(id)
                .filter(sa -> !Boolean.TRUE.equals(sa.getIsDeleted()))
                .orElseThrow(()-> new ResourceNotFoundException("Vendedor no encontrado"));
        if(req.getNroDocumento() == null || req.getNroDocumento().isBlank()){
            throw new BadRequestException("El número de documento es obligatorio");
        }

        if(req.getEmail() != null && repository.findByEmail(req.getEmail()).filter(s -> !s.getId().equals(id)).isPresent()){
            throw new BadRequestException("El correo electrónico ya está en uso");
        }

        if(req.getNroDocumento() != null && repository.findByNroDocumento(req.getNroDocumento()).filter(s -> !s.getId().equals(id)).isPresent()){
            throw new BadRequestException("El documento ya está en uso.");
        }

        District distrito = disRepository.findById(req.getDistritoId())
                .orElseThrow(() -> new ResourceNotFoundException("El distrito no existe"));

        Salesperson salesperson =mapToEntity(req, existing);
        salesperson.setDistrito(distrito);

        return toDto(repository.save(salesperson));
    }

    @Transactional(readOnly = true)
    public List<SalespersonResponse> listAll(){
        return repository.findAll()
                .stream()
                .filter(s -> !Boolean.TRUE.equals(s.getIsDeleted()))
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public SalespersonResponse getById(Integer id){
        Salesperson s = repository.findById(id)
                .filter(sa -> !Boolean.TRUE.equals(sa.getIsDeleted()))
                .orElseThrow(() -> new ResourceNotFoundException("Vendedor no encontrado"));
        return toDto(s);
    }

    @Transactional
    public void softDelete(Integer id){
        Salesperson salesperson = repository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Vendedor no encontrado"));
        if(Boolean.TRUE.equals(salesperson.getIsDeleted())){
            throw new BadRequestException("El vendedor ya fue eliminado");
        }
        salesperson.softDelete();
        repository.save(salesperson);
    }

    private SalespersonResponse toDto(Salesperson s){
        return new SalespersonResponse(
                s.getId(),
                s.getName(),
                s.getApellidos(),
                s.getNroDocumento(),
                s.getDireccion(),
                s.getDistrito() != null ? s.getDistrito().getName() : null,
                s.getProvincia() != null ? s.getProvincia().getName() : null,
                s.getDepartamento() != null ? s.getDepartamento().getName() : null,
                s.getTelefono(),
                s.getEmail());
    }

    private Salesperson mapToEntity(SalespersonRequest dto, Salesperson sal){
        sal.setName(dto.getName());
        sal.setApellidos(dto.getApellidos());
        sal.setNroDocumento(dto.getNroDocumento());
        sal.setDireccion(dto.getDireccion());
        sal.setTelefono(dto.getTelefono());
        sal.setEmail(dto.getEmail());
        return sal;
    }


}
