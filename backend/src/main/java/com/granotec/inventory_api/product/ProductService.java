package com.granotec.inventory_api.product;

import com.granotec.inventory_api.common.exception.ResourceNotFoundException;
import com.granotec.inventory_api.product.dto.ProductResponse;
import com.granotec.inventory_api.product.dto.ProductStockDetailsResponse;
import com.granotec.inventory_api.stock.StockRepository;
import com.granotec.inventory_api.stock.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final StockRepository stockRepository;
    private final StockService warehouseStockService;

    public Page<ProductResponse> listAll(int page, int size, String q) {
        Pageable p = PageRequest.of(page, size);
        Page<Product> pageRes;
        if (q == null || q.isBlank()) {
            pageRes = productRepository.findAll(p);
        } else {
            pageRes = productRepository.findByNameContainingIgnoreCaseOrCodeContainingIgnoreCase(q, q, p);
        }
        return pageRes.map(this::toDto);
    }

    public ProductResponse getById(Integer id){
        Product prod = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
        return toDto(prod);
    }

    public ProductStockDetailsResponse getStockDetails(Integer productId){
        productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
        var total = stockRepository.sumCantidadByProductoId(productId);
        var lines = stockRepository.findByProductoId(productId).stream().map(s -> new ProductStockDetailsResponse.StockLine(s.getId(), s.getAlmacen().getId(), s.getLote(), s.getCantidad())).collect(Collectors.toList());
        return new ProductStockDetailsResponse(productId, total, lines);
    }

    public ProductResponse toDto(Product p){
        var total = stockRepository.sumCantidadByProductoId(p.getId());
        return new ProductResponse(p.getId(), p.getCode(), p.getName(), p.getDescription(), p.getPrice(), p.getUnitOfMeasure(), p.getIsLocked(), total);
    }

    public void setLock(Integer id, boolean locked, String reason){
        Product p = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
        p.setIsLocked(locked);
        p.setLockReason(reason);
        productRepository.save(p);
    }

    public void adjustStockForProduct(Integer productId, Long almacenId, String lote, java.math.BigDecimal delta, String notes, String username){
        // delegate to central stock service (it handles creating stock records and audit)
        warehouseStockService.recordAdjustment(almacenId, productId, lote, delta, notes, username);
    }

    // --- Centralized product-specific stock operations ---

    @Transactional
    public void decreaseStock(Integer productId, BigDecimal cantidad) {
        Product p = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
        if (Boolean.TRUE.equals(p.getIsLocked())) {
            throw new ResourceNotFoundException("Producto bloqueado y no permite movimientos");
        }
        warehouseStockService.decreaseByProduct(productId, cantidad);
    }

    @Transactional
    public void increaseStock(Integer productId, BigDecimal cantidad) {
        productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
        warehouseStockService.increaseByProduct(productId, cantidad);
    }

    @Transactional
    public void adjustStock(Integer oldProductId, int oldQty, Integer newProductId, BigDecimal newQty) {
        warehouseStockService.adjustStock(oldProductId, oldQty, newProductId, newQty);
    }
}
