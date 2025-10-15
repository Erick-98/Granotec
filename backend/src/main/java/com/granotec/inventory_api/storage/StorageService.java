package com.granotec.inventory_api.storage;

import com.granotec.inventory_api.common.exception.BadRequestException;
import com.granotec.inventory_api.common.exception.ResourceNotFoundException;
import com.granotec.inventory_api.storage.dto.StorageRequest;
import com.granotec.inventory_api.storage.dto.StorageResponse;
import com.granotec.inventory_api.storage.entity.Storage;
import com.granotec.inventory_api.storage.entity.StorageName;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StorageService {

    private final StorageRepository repository;

    public StorageResponse create(StorageRequest request){
        Storage sto = Storage.builder()
                .descripcion(request.getDescripcion())
                .nombre(parseNombre(request.getNombre()))
                .build();
        sto = repository.save(sto);
        return toDto(sto);
    }

    public StorageResponse update(Long id, StorageRequest request){
        Storage sto = repository.findById(id)
                .filter(a -> !Boolean.TRUE.equals(a.getIsDeleted()))
                .orElseThrow(()-> new ResourceNotFoundException("Almacen",id));
        sto.setDescripcion(request.getDescripcion());
        sto.setNombre(parseNombre(request.getNombre()));
        sto = repository.save(sto);
        return toDto(sto);
    }

    public StorageResponse getById(Long id) {
        Storage sto = repository.findById(id)
                .filter(a -> !Boolean.TRUE.equals(a.getIsDeleted()))
                .orElseThrow(() -> new ResourceNotFoundException("Almacen", id));
        return toDto(sto);
    }

    public List<StorageResponse> listAll() {
        return repository.findAllByIsDeletedFalse()
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    public void softDelete(Long id) {
        Storage sto = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Almacen", id));
        if (Boolean.TRUE.equals(sto.getIsDeleted())) {
            throw new BadRequestException("Almacen already deleted");
        }
        sto.softDelete();
        repository.save(sto);
    }


    private StorageResponse toDto(Storage sto) {
        return new StorageResponse(sto.getId(), sto.getNombre().name(), sto.getDescripcion());
    }

    private StorageName parseNombre(String nombre) {
        try {
            return StorageName.valueOf(nombre.trim().toUpperCase().replace(" ", "_"));
        } catch (Exception ex) {
            throw new BadRequestException("Invalid almacen nombre: " + nombre);
        }
    }

}
