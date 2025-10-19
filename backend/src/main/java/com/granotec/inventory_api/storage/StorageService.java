package com.granotec.inventory_api.storage;

import com.granotec.inventory_api.common.exception.BadRequestException;
import com.granotec.inventory_api.common.exception.ResourceNotFoundException;
import com.granotec.inventory_api.storage.dto.StorageRequest;
import com.granotec.inventory_api.storage.dto.StorageResponse;
import com.granotec.inventory_api.storage.entity.Storage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StorageService {

    private final StorageRepository repository;

    public StorageResponse create(StorageRequest request){

        String nombreNormalizado = normalize(request.getNombre());

        if(nombreNormalizado == null || nombreNormalizado.isBlank()){
            throw new BadRequestException("Ingrese el nombre del almacen");
        }

        if(repository.existsByNombreIgnoreCaseAndIsDeletedFalse(nombreNormalizado)){
            throw new BadRequestException(("El almacen ya está registrado"));
        }

        Storage sto = Storage.builder()
                .descripcion(request.getDescripcion().trim())
                .nombre(nombreNormalizado)
                .build();
        repository.save(sto);
        return toDto(sto);
    }

    public StorageResponse update(Long id, StorageRequest request){
        Storage sto = repository.findById(id)
                .filter(a -> !Boolean.TRUE.equals(a.getIsDeleted()))
                .orElseThrow(()-> new ResourceNotFoundException("El almacen no existe"));

        if (Boolean.TRUE.equals(sto.getIsDeleted())) {
            throw new ResourceNotFoundException("El almacen ya fue eliminado");
        }

        String nombrenormalizado = normalize(request.getNombre());
        if(nombrenormalizado == null || nombrenormalizado.isBlank()){
            throw new BadRequestException("El nombre del almacen es obligatorio");
        }

        Optional<Storage> existing = repository.findByNombreIgnoreCaseAndIsDeletedFalse(nombrenormalizado);
        if(existing.isPresent() && !existing.get().getId().equals(id)){
            throw new BadRequestException("El almacen ya está registrado");
        }

        sto.setDescripcion(request.getDescripcion().trim());
        sto.setNombre(nombrenormalizado);
        repository.save(sto);
        return toDto(sto);
    }

    public StorageResponse getById(Long id) {
        Storage sto = repository.findById(id)
                .filter(a -> !Boolean.TRUE.equals(a.getIsDeleted()))
                .orElseThrow(() -> new ResourceNotFoundException("El almacen no existe"));
        return toDto(sto);
    }

    public List<StorageResponse> listAll() {
        return repository.findAllByIsDeletedFalse()
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    public void softDelete(Long id) {
        Storage sto = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("El almacen no existe"));
        if (Boolean.TRUE.equals(sto.getIsDeleted())) {
            throw new BadRequestException("El almacen ya fue eliminado");
        }
        sto.softDelete();
        repository.save(sto);
    }


    private StorageResponse toDto(Storage sto) {
        return StorageResponse.builder()
                .id(sto.getId())
                .nombre(sto.getNombre())
                .descripcion(sto.getDescripcion())
                .build();
    }

    private String normalize(String input){
        return input == null ? null : input.trim().replaceAll("\\s+"," ");
    }


}
