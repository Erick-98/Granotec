package com.granotec.inventory_api.product.familiaProducto;

import com.granotec.inventory_api.common.exception.BadRequestException;
import com.granotec.inventory_api.common.exception.ResourceNotFoundException;
import com.granotec.inventory_api.product.familiaProducto.dto.familyProductRequest;
import com.granotec.inventory_api.product.familiaProducto.dto.familyProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class familiaProductoService {

    private final familiaProductoRepository repository;

    public familyProductResponse create(familyProductRequest request){

        String nombreNormalizado = normalize(request.getNombre());

        if(nombreNormalizado == null || nombreNormalizado.isBlank()){
            throw new BadRequestException("Ingrese el nombre de la familia de producto");
        }

        if(repository.existsByNombreIgnoreCaseAndIsDeletedFalse(nombreNormalizado)){
            throw new BadRequestException(("La familia de producto ya está registrada"));
        }

        familiaProducto fmp = familiaProducto.builder()
                .descripcion(request.getDescripcion().trim())
                .nombre(nombreNormalizado)
                .build();
        repository.save(fmp);
        return toDto(fmp);
    }

    public familyProductResponse update(Long id, familyProductRequest request){
        familiaProducto fmp = repository.findById(id)
                .filter(a -> !Boolean.TRUE.equals(a.getIsDeleted()))
                .orElseThrow(()-> new ResourceNotFoundException("La familia de producto no existe"));

        if (Boolean.TRUE.equals(fmp.getIsDeleted())) {
            throw new ResourceNotFoundException("La familia de producto ya fue eliminado");
        }

        String nombrenormalizado = normalize(request.getNombre());
        if(nombrenormalizado == null || nombrenormalizado.isBlank()){
            throw new BadRequestException("El nombre de la familia de producto es obligatorio");
        }

        Optional<familiaProducto> existing = repository.findByNombreIgnoreCaseAndIsDeletedFalse(nombrenormalizado);
        if(existing.isPresent() && !existing.get().getId().equals(id)){
            throw new BadRequestException("La familia de producto ya está registrado");
        }

        fmp.setDescripcion(request.getDescripcion().trim());
        fmp.setNombre(nombrenormalizado);
        repository.save(fmp);
        return toDto(fmp);
    }

    public familyProductResponse getById(Long id) {
        familiaProducto fmp = repository.findById(id)
                .filter(a -> !Boolean.TRUE.equals(a.getIsDeleted()))
                .orElseThrow(() -> new ResourceNotFoundException("El almacen no existe"));
        return toDto(fmp);
    }

    public List<familyProductResponse> listAll() {
        return repository.findAllByIsDeletedFalse()
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    public void softDelete(Long id) {
        familiaProducto fmp = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("La familia de producto no existe"));
        if (Boolean.TRUE.equals(fmp.getIsDeleted())) {
            throw new BadRequestException("La familia de producto ya fue eliminado");
        }
        fmp.softDelete();
        repository.save(fmp);
    }


    private familyProductResponse toDto(familiaProducto fmp) {
        return familyProductResponse.builder()
                .id(fmp.getId())
                .nombre(fmp.getNombre())
                .descripcion(fmp.getDescripcion())
                .build();
    }

    private String normalize(String input){
        return input == null ? null : input.trim().replaceAll("\\s+"," ");
    }


}
