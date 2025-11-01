package com.granotec.inventory_api.dispatch.details_dispatch;

import com.granotec.inventory_api.common.exception.BadRequestException;
import com.granotec.inventory_api.common.exception.ResourceNotFoundException;
import com.granotec.inventory_api.dispatch.details_dispatch.dto.DetailsDispatchRequest;
import com.granotec.inventory_api.dispatch.details_dispatch.dto.DetailsDispatchResponse;
import com.granotec.inventory_api.dispatch.DispatchRepository;
import com.granotec.inventory_api.product.ProductRepository;
import com.granotec.inventory_api.product.Product;
import com.granotec.inventory_api.stock.StockService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DetailsDispatchService {

    private final DetailsDispatchRepository repository;
    private final DispatchRepository dispatchRepository;
    private final ProductRepository productRepository;
    private final StockService stockService;

    @Transactional
    public DetailsDispatchResponse create(DetailsDispatchRequest req) {
        var disp = dispatchRepository.findById(req.getIdDespacho())
                .orElseThrow(() -> new BadRequestException("Despacho no encontrado"));
        Product p = productRepository.findById(req.getIdProducto())
                .orElseThrow(() -> new BadRequestException("Producto no encontrado"));

        // stock validation and decrement via StockService
        stockService.decreaseByProduct(p.getId(), req.getCantidad());

        DetailsDispatch dd = new DetailsDispatch();
        dd.setDispatch(disp);
        dd.setProduct(p);
        dd.setCantidad(req.getCantidad());

        dd = repository.save(dd);
        return toDto(dd);
    }

    @Transactional
    public DetailsDispatchResponse update(Integer id, DetailsDispatchRequest req) {
        DetailsDispatch dd = repository.findById(id)
                .filter(x -> !Boolean.TRUE.equals(x.getIsDeleted()))
                .orElseThrow(() -> new ResourceNotFoundException("Detalle de despacho no encontrado"));

        Product oldProduct = dd.getProduct();
        int oldQty = dd.getCantidad() != null ? dd.getCantidad().intValue() : 0;

        Product newProduct = productRepository.findById(req.getIdProducto())
                .orElseThrow(() -> new BadRequestException("Producto no encontrado"));

        // restore old stock and decrease new stock explicitly
        if (oldProduct != null && oldQty > 0) {
            stockService.increaseByProduct(oldProduct.getId(), new java.math.BigDecimal(oldQty));
        }
        if (newProduct != null && req.getCantidad() != null) {
            stockService.decreaseByProduct(newProduct.getId(), req.getCantidad());
        }

        var disp = dispatchRepository.findById(req.getIdDespacho()).orElseThrow(() -> new BadRequestException("Despacho no encontrado"));
        dd.setDispatch(disp);
        dd.setProduct(newProduct);
        dd.setCantidad(req.getCantidad());

        dd = repository.save(dd);
        return toDto(dd);
    }

    public List<DetailsDispatchResponse> listAll() {
        return repository.findAll()
                .stream()
                .filter(x -> !Boolean.TRUE.equals(x.getIsDeleted()))
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public DetailsDispatchResponse getById(Integer id) {
        DetailsDispatch dd = repository.findById(id)
                .filter(x -> !Boolean.TRUE.equals(x.getIsDeleted()))
                .orElseThrow(() -> new ResourceNotFoundException("Detalle de despacho no encontrado"));
        return toDto(dd);
    }

    @Transactional
    public void softDelete(Integer id) {
        DetailsDispatch dd = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Detalle de despacho no encontrado"));
        if (Boolean.TRUE.equals(dd.getIsDeleted())) {
            throw new BadRequestException("Detalle ya eliminado");
        }
        // restore stock via StockService
        if (dd.getProduct() != null && dd.getCantidad() != null) {
            stockService.increaseByProduct(dd.getProduct().getId(), dd.getCantidad());
        }
        dd.softDelete();
        repository.save(dd);
    }

    public DetailsDispatchResponse toDto(DetailsDispatch dd) {
        return new DetailsDispatchResponse(
                dd.getId(),
                dd.getDispatch() != null ? dd.getDispatch().getId() : null,
                dd.getProduct() != null ? dd.getProduct().getId() : null,
                dd.getCantidad()
        );
    }
}
