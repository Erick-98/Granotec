package com.granotec.inventory_api.ov.details_ov;

import com.granotec.inventory_api.common.exception.BadRequestException;
import com.granotec.inventory_api.common.exception.ResourceNotFoundException;
import com.granotec.inventory_api.ov.details_ov.dto.DetailsOvRequest;
import com.granotec.inventory_api.ov.details_ov.dto.DetailsOvResponse;
import com.granotec.inventory_api.ov.OvRepository;
import com.granotec.inventory_api.product.ProductRepository;
import com.granotec.inventory_api.product.Product;
import com.granotec.inventory_api.stock.StockService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DetailsOvService {

    private final DetailsOvRepository repository;
    private final OvRepository ovRepository;
    private final ProductRepository productRepository;
    private final StockService stockService;

    @Transactional
    public DetailsOvResponse create(DetailsOvRequest req) {
        var ov = ovRepository.findById(req.getIdOv())
                .orElseThrow(() -> new BadRequestException("Orden de venta no encontrada"));
        Product p = productRepository.findById(req.getIdProducto())
                .orElseThrow(() -> new BadRequestException("Producto no encontrado"));

        // stock validation and decrement via StockService (by product)
        stockService.decreaseByProduct(p.getId(), req.getCantidad());

        Details_ov d = new Details_ov();
        d.setOrdenVenta(ov);
        d.setProduct(p);
        d.setCantidad(req.getCantidad());
        d.setPrecioUnitario(req.getPrecioUnitario());
        d.setSubtotal(req.getCantidad().multiply(req.getPrecioUnitario()));

        d = repository.save(d);
        return toDto(d);
    }

    @Transactional
    public DetailsOvResponse update(Integer id, DetailsOvRequest req) {
        Details_ov d = repository.findById(id)
                .filter(x -> !Boolean.TRUE.equals(x.getIsDeleted()))
                .orElseThrow(() -> new ResourceNotFoundException("Detalle de OV no encontrado"));

        Product oldProduct = d.getProduct();
        int oldQty = d.getCantidad() != null ? d.getCantidad().intValue() : 0;

        Product newProduct = productRepository.findById(req.getIdProducto())
                .orElseThrow(() -> new BadRequestException("Producto no encontrado"));

        // restore old stock and decrease new stock explicitly
        if (oldProduct != null && oldQty > 0) {
            stockService.increaseByProduct(oldProduct.getId(), new BigDecimal(oldQty));
        }
        if (newProduct != null && req.getCantidad() != null) {
            stockService.decreaseByProduct(newProduct.getId(), req.getCantidad());
        }

        var ov = ovRepository.findById(req.getIdOv()).orElseThrow(() -> new BadRequestException("Orden de venta no encontrada"));
        d.setOrdenVenta(ov);
        d.setProduct(newProduct);
        d.setCantidad(req.getCantidad());
        d.setPrecioUnitario(req.getPrecioUnitario());
        d.setSubtotal(req.getCantidad().multiply(req.getPrecioUnitario()));

        d = repository.save(d);
        return toDto(d);
    }

    public List<DetailsOvResponse> listAll() {
        return repository.findAll()
                .stream()
                .filter(x -> !Boolean.TRUE.equals(x.getIsDeleted()))
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<DetailsOvResponse> listByOvId(Integer ovId) {
        return repository.findAll()
                .stream()
                .filter(x -> !Boolean.TRUE.equals(x.getIsDeleted()))
                .filter(d -> d.getOrdenVenta() != null && d.getOrdenVenta().getId().equals(ovId))
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public DetailsOvResponse getById(Integer id) {
        Details_ov d = repository.findById(id)
                .filter(x -> !Boolean.TRUE.equals(x.getIsDeleted()))
                .orElseThrow(() -> new ResourceNotFoundException("Detalle de OV no encontrado"));
        return toDto(d);
    }

    @Transactional
    public void softDelete(Integer id) {
        Details_ov d = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Detalle de OV no encontrado"));
        if (Boolean.TRUE.equals(d.getIsDeleted())) {
            throw new BadRequestException("Detalle ya eliminado");
        }
        // restore stock via StockService
        if (d.getProduct() != null && d.getCantidad() != null) {
            stockService.increaseByProduct(d.getProduct().getId(), d.getCantidad());
        }
        d.softDelete();
        repository.save(d);
    }

    public DetailsOvResponse toDto(Details_ov d) {
        return new DetailsOvResponse(
                d.getId(),
                d.getOrdenVenta() != null ? d.getOrdenVenta().getId() : null,
                d.getProduct() != null ? d.getProduct().getId() : null,
                d.getCantidad(),
                d.getPrecioUnitario(),
                d.getSubtotal()
        );
    }
}
