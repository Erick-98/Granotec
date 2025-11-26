package com.granotec.inventory_api.customer.typeCustomer;

import com.granotec.inventory_api.common.exception.BadRequestException;
import com.granotec.inventory_api.common.exception.ResourceNotFoundException;
import com.granotec.inventory_api.customer.typeCustomer.dto.TypeCustomerRequest;
import com.granotec.inventory_api.customer.typeCustomer.dto.TypeCustomerResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TypeCustomerService {

    private final TypeCustomerRepository repository;

    public TypeCustomerResponse create(TypeCustomerRequest request){
        String nombreNormalizado = normalize(request.getNombre());

        if(nombreNormalizado == null || nombreNormalizado.isBlank()){
            throw new BadRequestException("Ingrese el nombre del tipo de cliente");
        }

        if(repository.existsByNombreIgnoreCaseAndIsDeletedFalse(nombreNormalizado)){
            throw new BadRequestException("El tipo de cliente ya está registrado");
        }

        TypeCustomer tpc = TypeCustomer.builder()
                .descripcion(request.getDescripcion().trim())
                .nombre(nombreNormalizado)
                .build();
        repository.save(tpc);
        return toDto(tpc);
    }

    public TypeCustomerResponse update(Long id, TypeCustomerRequest request){
        TypeCustomer tpc = repository.findById(id)
                .filter(a -> !Boolean.TRUE.equals(a.getIsDeleted()))
                .orElseThrow(()-> new ResourceNotFoundException("El tipo de cliente no existe"));

        if (Boolean.TRUE.equals(tpc.getIsDeleted())) {
            throw new ResourceNotFoundException("El tipo de cliente ya fue eliminado");
        }

        String nombrenormalizado = normalize(request.getNombre());
        if(nombrenormalizado == null || nombrenormalizado.isBlank()){
            throw new BadRequestException("El nombre del almacen es obligatorio");
        }

        Optional<TypeCustomer> existing = repository.findByNombreIgnoreCaseAndIsDeletedFalse(nombrenormalizado);
        if(existing.isPresent() && !existing.get().getId().equals(id)){
            throw new BadRequestException("El tipo de cliente ya está registrado");
        }

        tpc.setDescripcion(request.getDescripcion().trim());
        tpc.setNombre(nombrenormalizado);
        repository.save(tpc);
        return toDto(tpc);
    }

    public TypeCustomerResponse getById(Long id) {
        TypeCustomer tpc = repository.findById(id)
                .filter(a -> !Boolean.TRUE.equals(a.getIsDeleted()))
                .orElseThrow(() -> new ResourceNotFoundException("El tipo de cliente no existe"));
        return toDto(tpc);
    }

    public List<TypeCustomerResponse> listAll() {
        return repository.findAllByIsDeletedFalse()
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    public void softDelete(Long id) {
        TypeCustomer tpc = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("El tipo de cliente no existe"));
        if (Boolean.TRUE.equals(tpc.getIsDeleted())) {
            throw new BadRequestException("El tipo de cliente ya fue eliminado");
        }
        tpc.softDelete();
        repository.save(tpc);
    }

    private TypeCustomerResponse toDto(TypeCustomer tpc) {
        return TypeCustomerResponse.builder()
                .id(tpc.getId())
                .nombre(tpc.getNombre())
                .descripcion(tpc.getDescripcion())
                .build();
    }

    private String normalize(String input){
        return input == null ? null : input.trim().replaceAll("\\s+"," ");
    }







}
