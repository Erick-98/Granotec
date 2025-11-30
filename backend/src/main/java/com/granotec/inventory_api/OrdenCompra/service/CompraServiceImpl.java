package com.granotec.inventory_api.OrdenCompra.service;

import com.granotec.inventory_api.Kardex.KardexRequest;
import com.granotec.inventory_api.Kardex.KardexService;
import com.granotec.inventory_api.Lote.Lote;
import com.granotec.inventory_api.Lote.LoteRepository;
import com.granotec.inventory_api.OrdenCompra.CompraMapperHelper;
import com.granotec.inventory_api.OrdenCompra.OrdenCompra;
import com.granotec.inventory_api.OrdenCompra.detalle.DetalleOrdenCompraRepository;
import com.granotec.inventory_api.OrdenCompra.dto.CompraRequest;
import com.granotec.inventory_api.OrdenCompra.dto.CompraResponse;
import com.granotec.inventory_api.StockLote.StockLote;
import com.granotec.inventory_api.common.enums.TipoMovimiento;
import com.granotec.inventory_api.common.enums.TypeOperation;
import com.granotec.inventory_api.common.exception.BadRequestException;
import com.granotec.inventory_api.OrdenCompra.detalle.DetalleOrdenCompra;
import com.granotec.inventory_api.product.Product;
import com.granotec.inventory_api.storage.Storage;
import com.granotec.inventory_api.vendor.Vendor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.granotec.inventory_api.OrdenCompra.OrdenCompraRepository;
import com.granotec.inventory_api.StockLote.StockLoteRepository;
import com.granotec.inventory_api.product.ProductRepository;
import com.granotec.inventory_api.vendor.VendorRepository;
import com.granotec.inventory_api.storage.StorageRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CompraServiceImpl implements CompraService {
     private final OrdenCompraRepository ordenCompraRepository;
     private final VendorRepository vendorRepository;
     private final StockLoteRepository stockLoteRepository;
     private final StorageRepository storageRepository;
     private final ProductRepository productRepository;
     private final KardexService kardexService;
     private final LoteRepository loteRepository;
     private final CompraMapperHelper compraMapperHelper;
     private final DetalleOrdenCompraRepository detalleOrdenCompraRepository;

    @Override
    @Transactional
    public CompraResponse registrarCompra(CompraRequest request) {

        Vendor proveedor = vendorRepository.findById(request.getProveedorId())
                .orElseThrow(() -> new BadRequestException("Proveedor no encontrado"));
        Storage almacen = storageRepository.findById(request.getAlmacenId())
                .orElseThrow(() -> new BadRequestException("Almacén no encontrado"));

        OrdenCompra ordenCompra = OrdenCompra.builder()
                .numero(request.getNumeroFactura())
                .proveedor(proveedor)
                .almacen(almacen)
                .fecha(LocalDate.now())
                .total(BigDecimal.ZERO)
                .build();

        ordenCompraRepository.save(ordenCompra);

        BigDecimal totalCompra = BigDecimal.ZERO;

        for (CompraRequest.DetalleCompraDTO det : request.getDetalles()) {

            Product producto = productRepository.findById(det.getProductoId())
                    .orElseThrow(() -> new BadRequestException("Producto no encontrado"));

            DetalleOrdenCompra detalle = DetalleOrdenCompra.builder()
                    .ordenCompra(ordenCompra)
                    .producto(producto)
                    .cantidadOrdenada(det.getCantidadOrdenada())
                    .cantidadRecibida(det.getCantidadRecibida())
                    .precioUnitario(det.getPrecioUnitario())
                    .subtotal(det.getCantidadOrdenada().multiply(det.getPrecioUnitario()))
                    .build();

            detalleOrdenCompraRepository.save(detalle);
            ordenCompra.getDetalles().add(detalle);
            totalCompra = totalCompra.add(detalle.getSubtotal());


            Lote lote = Lote.builder()
                    .ordenProduccion(null)
                    .producto(producto)
                    .codigoLote(det.getLote())
                    .fechaProduccion(det.getFechaProduccion())
                    .fechaVencimiento(det.getFechaVencimiento())
                    .cantidadProducida(BigDecimal.valueOf(0))
                    .costoTotal(det.getCantidadOrdenada().multiply(det.getPrecioUnitario()))
                    .costoUnitario(det.getPrecioUnitario())
                    //Por ahora no se considera la ganancia para el precio de venta
                    .precioVentaUnitario(det.getPrecioUnitario())
                    .estado("DISPONIBLE")
                    .build();

            loteRepository.save(lote);

            detalle.setLote(lote);
            detalleOrdenCompraRepository.save(detalle);

            StockLote stockLote = StockLote.builder()
                    .lote(lote)
                    .almacen(almacen)
                    .cantidadDisponible(det.getCantidadRecibida())
                    .build();

            stockLoteRepository.save(stockLote);


            // Registrar entrada en Kardex (usar stockAnterior real)
            KardexRequest requestK = new KardexRequest();
            requestK.setAlmacenId(request.getAlmacenId());
            requestK.setProductoId(det.getProductoId());
            requestK.setLote(det.getLote());
            requestK.setNumeroOp(null);
            requestK.setTipoMovimiento(TipoMovimiento.ENTRADA);
            requestK.setTipoOperacion(TypeOperation.COMPRA);
            requestK.setReferencia(request.getNumeroFactura());
            requestK.setCantidad(det.getCantidadRecibida());
            requestK.setCostoUnitarioSoles(det.getPrecioUnitario());
            requestK.setTasaCambio(BigDecimal.valueOf(3.5));
            requestK.setObservacion(null);
            requestK.setUsuarioId(1);

            kardexService.registrarMovimientos(requestK);
        }

        ordenCompra.setTotal(totalCompra);
        ordenCompraRepository.save(ordenCompra);

        return compraMapperHelper.toDto(ordenCompra);
    }

    @Override
    public CompraResponse obtenerCompra(Integer id) {
        OrdenCompra ordenCompra = ordenCompraRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Compra no encontrada"));
        return compraMapperHelper.toDto(ordenCompra);
    }

    @Override
    public List<CompraResponse> listarCompras() {
        return ordenCompraRepository.findAll().stream()
                .map(compraMapperHelper::toDto)
                .toList();
    }

}
