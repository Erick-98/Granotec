package com.granotec.inventory_api.product;

import com.granotec.inventory_api.common.exception.BadRequestException;
import com.granotec.inventory_api.common.exception.ResourceNotFoundException;
import com.granotec.inventory_api.product.dto.ProductRequest;
import com.granotec.inventory_api.product.dto.ProductResponse;
import com.granotec.inventory_api.product.dto.ProductStockDetailsResponse;
import com.granotec.inventory_api.vendor.Vendor;
import com.granotec.inventory_api.vendor.VendorRepository;
import com.granotec.inventory_api.product.familiaProducto.familiaProducto;
import com.granotec.inventory_api.product.familiaProducto.familiaProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final VendorRepository vendorRepository;
    private final familiaProductoRepository familiaRepository;

    public Page<ProductResponse> listAll(int page, int size, String q) {
        Pageable pageable = PageRequest.of(page, size);
        if (q == null || q.trim().isEmpty()) {
            Page<Product> p = productRepository.findAll(pageable);
            List<ProductResponse> list = p.getContent().stream().map(this::toDto).collect(Collectors.toList());
            return new PageImpl<>(list, pageable, p.getTotalElements());
        } else {
            Page<Product> p = productRepository.findByNombreComercialContainingIgnoreCaseOrCodeContainingIgnoreCase(q, q, pageable);
            List<ProductResponse> list = p.getContent().stream().map(this::toDto).collect(Collectors.toList());
            return new PageImpl<>(list, pageable, p.getTotalElements());
        }
    }

    public ProductResponse getById(Integer id) {
        Product prod = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
        return toDto(prod);
    }

    @Transactional
    public ProductResponse create(ProductRequest request){
        if(productRepository.findByCode(request.getCode()).isPresent()){
            throw new BadRequestException("El código ya existe");
        }
        Product p = new Product();
        p.setCode(request.getCode());
        p.setNombreComercial(request.getNombreComercial());
        p.setDescription(request.getDescription());
        if(request.getProveedorId() != null){
            Vendor v = vendorRepository.findById(request.getProveedorId()).orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado"));
            p.setProveedor(v);
        }
        if(request.getFamiliaId() != null){
            familiaProducto f = familiaRepository.findById(request.getFamiliaId()).orElseThrow(() -> new ResourceNotFoundException("Familia de producto no encontrada"));
            p.setFamilia(f);
        }
        p.setTipoPresentacion(request.getTipoPresentacion());
        p.setUnitOfMeasure(request.getUnitOfMeasure());
        p.setIsLocked(request.getBlocked() != null ? request.getBlocked() : Boolean.FALSE);
        p = productRepository.save(p);
        return toDto(p);
    }

    @Transactional
    public ProductResponse update(Integer id, ProductRequest request){
        Product p = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
        if(request.getCode() != null && !request.getCode().equals(p.getCode())){
            if(productRepository.findByCode(request.getCode()).isPresent()){
                throw new BadRequestException("El código ya existe");
            }
            p.setCode(request.getCode());
        }
        p.setNombreComercial(request.getNombreComercial());
        p.setDescription(request.getDescription());
        if(request.getProveedorId() != null){
            Vendor v = vendorRepository.findById(request.getProveedorId()).orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado"));
            p.setProveedor(v);
        } else {
            p.setProveedor(null);
        }
        if(request.getFamiliaId() != null){
            familiaProducto f = familiaRepository.findById(request.getFamiliaId()).orElseThrow(() -> new ResourceNotFoundException("Familia de producto no encontrada"));
            p.setFamilia(f);
        } else {
            p.setFamilia(null);
        }
        p.setTipoPresentacion(request.getTipoPresentacion());
        p.setUnitOfMeasure(request.getUnitOfMeasure());
        p.setIsLocked(request.getBlocked() != null ? request.getBlocked() : p.getIsLocked());
        p = productRepository.save(p);
        return toDto(p);
    }

    @Transactional
    public void softDelete(Integer id){
        Product p = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
        p.setIsDeleted(Boolean.TRUE);
        productRepository.save(p);
    }

    @Transactional
    public void setBlock(Integer id, boolean blocked, String reason){
        Product p = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
        p.setIsLocked(blocked);
        if(reason != null) p.setLockReason(reason);
        productRepository.save(p);
    }

    public ProductStockDetailsResponse getStockDetails(Integer id){
        // stub: implement real stock aggregation based on your stock tables
        Product p = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
        ProductStockDetailsResponse resp = new ProductStockDetailsResponse();
        resp.setProductId(p.getId());
        resp.setTotalQuantity(BigDecimal.ZERO);
        resp.setLines(Collections.emptyList());
        return resp;
    }

    @Transactional
    public void adjustStockForProduct(Integer id, Long almacenId, String lote, BigDecimal delta, String notes, String user){
        Product p = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
        if(Boolean.TRUE.equals(p.getIsLocked())){
            throw new BadRequestException("El producto está bloqueado y no permite movimientos de salida/ajuste");
        }
        if(delta == null){
            throw new BadRequestException("Delta es requerido para ajustar stock");
        }
        if(delta.compareTo(java.math.BigDecimal.ZERO) == 0){
            throw new BadRequestException("Delta debe ser distinto de cero");
        }

    }

    private ProductResponse toDto(Product p){
        ProductResponse resp = new ProductResponse();
        resp.setId(p.getId());
        resp.setCode(p.getCode());
        resp.setName(p.getNombreComercial());
        resp.setDescription(p.getDescription());
        resp.setUnitOfMeasure(p.getUnitOfMeasure());
        resp.setTipoPresentacion(p.getTipoPresentacion());
        resp.setProveedor(p.getProveedor() != null ? p.getProveedor().getRazonSocial() : null);
        resp.setFamilia(p.getFamilia() != null ? p.getFamilia().getNombre() : null);
        resp.setIsLocked(p.getIsLocked());
        return resp;
    }

}
