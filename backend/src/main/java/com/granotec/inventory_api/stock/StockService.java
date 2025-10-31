package com.granotec.inventory_api.stock;

import com.granotec.inventory_api.common.exception.BadRequestException;
import com.granotec.inventory_api.common.exception.ResourceNotFoundException;
import com.granotec.inventory_api.product.ProductRepository;
import com.granotec.inventory_api.stock.dto.StockRequest;
import com.granotec.inventory_api.stock.dto.StockResponse;
import com.granotec.inventory_api.storage.StorageRepository;
import com.granotec.inventory_api.storage.entity.Storage;
import com.granotec.inventory_api.product.Product;
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
    private final StockAdjustmentRepository adjustmentRepository;

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
        Stock s = stock_repository_get(id);
        // BaseEntity proporciona softDelete() y getters/setters con nombre isDeleted
        s.softDelete();
        stockRepository.save(s);
    }

    private Stock stock_repository_get(Long id){
        return stockRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Stock no encontrado"));
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

    // --- Nuevos métodos atómicos propuestos ---

    @Transactional
    public void decreaseByStockId(Long stockId, BigDecimal qty){
        if(qty == null || qty.compareTo(BigDecimal.ZERO) <= 0) throw new BadRequestException("Cantidad debe ser positiva");
        int affected = stockRepository.decreaseByIdIfSufficient(stockId, qty);
        if(affected == 0){
            throw new BadRequestException("Stock insuficiente o registro no encontrado");
        }
    }

    @Transactional
    public void decreaseByAlmacenProductoLote(Long almacenId, Integer productoId, String lote, BigDecimal qty){
        if(qty == null || qty.compareTo(BigDecimal.ZERO) <= 0) throw new BadRequestException("Cantidad debe ser positiva");
        int affected = stockRepository.decreaseByAlmacenProductoLoteIfSufficient(almacenId, productoId, lote, qty);
        if(affected == 0){
            throw new BadRequestException("Stock insuficiente o registro no encontrado");
        }
    }

    // Convenience: decrease by product (take first stock record for product)
    @Transactional
    public void decreaseByProduct(Integer productoId, BigDecimal qty){
        Stock s = stockRepository.findFirstByProductoIdOrderByIdAsc(productoId)
                .orElseThrow(() -> new BadRequestException("No existe stock para el producto"));
        decreaseByStockId(s.getId(), qty);
    }

    // Convenience: increase by product (take first stock record for product)
    @Transactional
    public void increaseByProduct(Integer productoId, BigDecimal qty){
        if(qty == null || qty.compareTo(BigDecimal.ZERO) <= 0) throw new BadRequestException("Cantidad debe ser positiva");
        Stock s = stockRepository.findFirstByProductoIdOrderByIdAsc(productoId)
                .orElseThrow(() -> new BadRequestException("No existe stock para el producto"));
        // use adjustStock to add delta positive
        adjustStock(s.getAlmacen().getId(), productoId, s.getLote(), qty);
    }

    // Overload: ajustar/restaurar por producto (compat con servicios de detalle)
    @Transactional
    public void adjustStock(Integer oldProductId, int oldQty, Integer newProductId, BigDecimal newQty){
        if(oldProductId != null && oldQty > 0){
            increaseByProduct(oldProductId, new java.math.BigDecimal(oldQty));
        }
        if(newProductId != null && newQty != null){
            decreaseByProduct(newProductId, newQty);
        }
    }

    // Registrar ajuste y ejecutar
    @Transactional
    public void recordAdjustment(Long idAlmacen, Integer idProducto, String lote, BigDecimal delta, String notes, String createdBy){
        // resolve storage and product existance
        Storage almacen = storageRepository.findById(idAlmacen).orElse(null);
        Product producto = null;
        if(idProducto != null) producto = productRepository.findById(idProducto).orElse(null);

        // apply adjustment to stock
        adjustStock(idAlmacen, idProducto, lote, delta);

        StockAdjustment adj = StockAdjustment.builder()
                .almacen(almacen)
                .producto(producto)
                .lote(lote)
                .delta(delta)
                .notes(notes)
                .createdBy(createdBy)
                .build();
        adjustmentRepository.save(adj);
    }

    private StockResponse toResponse(Stock s){
        return new StockResponse(s.getId(), s.getAlmacen().getId(), s.getProducto().getId(), s.getLote(), s.getCantidad());
    }
}
