package com.granotec.inventory_api.product;

import com.granotec.inventory_api.StockLote.StockLoteRepository;
import com.granotec.inventory_api.common.exception.BadRequestException;
import com.granotec.inventory_api.common.exception.ResourceNotFoundException;
import com.granotec.inventory_api.product.dto.ProductPriceResponse;
import com.granotec.inventory_api.product.dto.ProductRequest;
import com.granotec.inventory_api.product.dto.ProductResponse;
import com.granotec.inventory_api.product.dto.ProductStockDetailsResponse;
import com.granotec.inventory_api.storage.Storage;
import com.granotec.inventory_api.storage.StorageRepository;
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
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final VendorRepository vendorRepository;
    private final familiaProductoRepository familiaRepository;
    private final StockLoteRepository stockLoteRepository;
    private final StorageRepository storageRepository;

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
        p.setTipoProducto(request.getTipoProducto());
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
        p.setTipoProducto(request.getTipoProducto());
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

    /**
     * Calcula el precio promedio ponderado de un producto en un almacén específico
     * Este precio se basa en los lotes disponibles (con stock > 0)
     * Formula: Σ(cantidad_disponible * costo_unitario) / Σ(cantidad_disponible)
     */
    @Transactional(readOnly = true)
    public ProductPriceResponse calcularPrecioPromedio(Integer productoId, Long almacenId) {
        Product producto = productRepository.findById(productoId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

        Storage almacen = storageRepository.findById(almacenId)
                .orElseThrow(() -> new ResourceNotFoundException("Almacén no encontrado"));

        BigDecimal precioPromedio = stockLoteRepository.calcularPrecioPromedioPonderado(productoId, almacenId);
        BigDecimal stockDisponible = stockLoteRepository.sumDisponibleByProductoAndAlmacen(productoId, almacenId);

        if (precioPromedio == null) {
            precioPromedio = BigDecimal.ZERO;
        }

        // Redondear a 6 decimales para mantener precisión
        precioPromedio = precioPromedio.setScale(6, RoundingMode.HALF_UP);

        String mensaje = null;
        if (stockDisponible == null || stockDisponible.compareTo(BigDecimal.ZERO) == 0) {
            mensaje = "No hay stock disponible en este almacén";
            precioPromedio = BigDecimal.ZERO;
        }

        return ProductPriceResponse.builder()
                .productoId(producto.getId())
                .nombreProducto(producto.getNombreComercial())
                .almacenId(almacen.getId())
                .nombreAlmacen(almacen.getNombre())
                .precioPromedioPonderado(precioPromedio)
                .stockDisponible(stockDisponible != null ? stockDisponible : BigDecimal.ZERO)
                .mensaje(mensaje)
                .build();
    }

    /**
     * Calcula el precio promedio ponderado de un producto en todos los almacenes
     */
    @Transactional(readOnly = true)
    public ProductPriceResponse calcularPrecioPromedioGeneral(Integer productoId) {
        Product producto = productRepository.findById(productoId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

        BigDecimal precioPromedio = stockLoteRepository.calcularPrecioPromedioPonderadoGeneral(productoId);
        BigDecimal stockDisponible = stockLoteRepository.sumDisponibleByProducto(productoId);

        if (precioPromedio == null) {
            precioPromedio = BigDecimal.ZERO;
        }

        // Redondear a 6 decimales
        precioPromedio = precioPromedio.setScale(6, RoundingMode.HALF_UP);

        String mensaje = null;
        if (stockDisponible == null || stockDisponible.compareTo(BigDecimal.ZERO) == 0) {
            mensaje = "No hay stock disponible";
            precioPromedio = BigDecimal.ZERO;
        }

        return ProductPriceResponse.builder()
                .productoId(producto.getId())
                .nombreProducto(producto.getNombreComercial())
                .almacenId(null)
                .nombreAlmacen("TODOS")
                .precioPromedioPonderado(precioPromedio)
                .stockDisponible(stockDisponible != null ? stockDisponible : BigDecimal.ZERO)
                .mensaje(mensaje)
                .build();
    }

    /**
     * Obtiene un producto con su precio promedio ponderado incluido
     * Este método es útil para el frontend cuando necesita mostrar el producto con su precio
     */
    @Transactional(readOnly = true)
    public ProductResponse getByIdWithPrice(Integer id, Long almacenId) {
        Product prod = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

        ProductResponse response = toDto(prod);

        if (almacenId != null) {
            // Calcular precio promedio para el almacén específico
            BigDecimal precioPromedio = stockLoteRepository.calcularPrecioPromedioPonderado(id, almacenId);
            BigDecimal stock = stockLoteRepository.sumDisponibleByProductoAndAlmacen(id, almacenId);

            response.setPrecioPromedioPonderado(precioPromedio != null ? precioPromedio.setScale(6, RoundingMode.HALF_UP) : BigDecimal.ZERO);
            response.setStockTotal(stock != null ? stock : BigDecimal.ZERO);
        } else {
            // Calcular precio promedio general
            BigDecimal precioPromedio = stockLoteRepository.calcularPrecioPromedioPonderadoGeneral(id);
            BigDecimal stock = stockLoteRepository.sumDisponibleByProducto(id);

            response.setPrecioPromedioPonderado(precioPromedio != null ? precioPromedio.setScale(6, RoundingMode.HALF_UP) : BigDecimal.ZERO);
            response.setStockTotal(stock != null ? stock : BigDecimal.ZERO);
        }

        return response;
    }

    private ProductResponse toDto(Product p){
        ProductResponse resp = new ProductResponse();
        resp.setId(p.getId());
        resp.setCode(p.getCode());
        resp.setName(p.getNombreComercial());
        resp.setDescription(p.getDescription());
        resp.setUnitOfMeasure(p.getUnitOfMeasure());
        resp.setTipoPresentacion(p.getTipoPresentacion());
        resp.setTipoProducto(p.getTipoProducto());
        resp.setProveedor(p.getProveedor() != null ? p.getProveedor().getRazonSocial() : null);
        resp.setFamilia(p.getFamilia() != null ? p.getFamilia().getNombre() : null);
        resp.setIsLocked(p.getIsLocked());
        return resp;
    }

}
