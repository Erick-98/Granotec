package com.granotec.inventory_api.stock;

import com.granotec.inventory_api.common.exception.BadRequestException;
import com.granotec.inventory_api.common.exception.ResourceNotFoundException;
import com.granotec.inventory_api.product.ProductRepository;
import com.granotec.inventory_api.stock.dto.StockRequest;
import com.granotec.inventory_api.stock.dto.StockResponse;
import com.granotec.inventory_api.storage.StorageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;
    private final StorageRepository storageRepository;
    private final ProductRepository productRepository;

    public List<StockResponse> listAll(){
        return stockRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    public StockResponse getById(Long id){
        Stock s = stockRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Stock no encontrado"));
        return toResponse(s);
    }

    @Transactional
    public StockResponse create(StockRequest req){
        // validar existencia almacen/producto
        var almacen = storageRepository.findById(req.getIdAlmacen()).orElseThrow(() -> new ResourceNotFoundException("Almacen no encontrado"));
        var producto = productRepository.findById(req.getIdProducto()).orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

        // Verificar si existe stock existente
        var existing = stockRepository.findByAlmacenIdAndProductoIdAndLote(req.getIdAlmacen(), req.getIdProducto(), req.getLote());
        if(existing.isPresent()){
            throw new BadRequestException("Ya existe stock para ese almacen/producto/lote");
        }

        if(req.getCantidad().compareTo(BigDecimal.ZERO) < 0) throw new BadRequestException("cantidad no puede ser negativa");

        Stock s = Stock.builder()
                .almacen(almacen)
                .producto(producto)
                .lote(req.getLote())
                .cantidad(req.getCantidad())
                .build();

        Stock saved = stockRepository.save(s);
        return toResponse(saved);
    }

    @Transactional
    public StockResponse update(Long id, StockRequest req){
        Stock s = stockRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Stock no encontrado"));
        var almacen = storageRepository.findById(req.getIdAlmacen()).orElseThrow(() -> new ResourceNotFoundException("Almacen no encontrado"));
        var producto = productRepository.findById(req.getIdProducto()).orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

        if(req.getCantidad().compareTo(BigDecimal.ZERO) < 0) throw new BadRequestException("cantidad no puede ser negativa");

        s.setAlmacen(almacen);
        s.setProducto(producto);
        s.setLote(req.getLote());
        s.setCantidad(req.getCantidad());

        return toResponse(stockRepository.save(s));
    }

    @Transactional
    public void softDelete(Long id){
        Stock s = stockRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Stock no encontrado"));
        // BaseEntity proporciona softDelete() y getters/setters con nombre isDeleted
        s.softDelete();
        stockRepository.save(s);
    }

    // Ajuste de stock con bloqueo pesimista
    @Transactional
    public StockResponse adjustStock(Long idAlmacen, Integer idProducto, String lote, BigDecimal delta){
        if(delta == null) throw new BadRequestException("Delta es requerido");
        // obtener o crear con bloqueo
        Stock stock = stockRepository.findByAlmacenIdAndProductoIdAndLoteForUpdate(idAlmacen, idProducto, lote)
                .orElseGet(() -> {
                    // crear nuevo registro si delta positivo
                    if(delta.compareTo(BigDecimal.ZERO) < 0){
                        throw new BadRequestException("No existe stock para disminuir");
                    }
                    var almacen = storageRepository.findById(idAlmacen).orElseThrow(() -> new ResourceNotFoundException("Almacen no encontrado"));
                    var producto = productRepository.findById(idProducto).orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
                    Stock s = Stock.builder()
                            .almacen(almacen)
                            .producto(producto)
                            .lote(lote)
                            .cantidad(delta)
                            .build();
                    return stockRepository.save(s);
                });

        BigDecimal newQty = stock.getCantidad().add(delta);
        if(newQty.compareTo(BigDecimal.ZERO) < 0) throw new BadRequestException("Stock insuficiente");
        stock.setCantidad(newQty);
        Stock saved = stockRepository.save(stock);
        return toResponse(saved);
    }

    private StockResponse toResponse(Stock s){
        return new StockResponse(s.getId(), s.getAlmacen().getId(), s.getProducto().getId(), s.getLote(), s.getCantidad());
    }
}
