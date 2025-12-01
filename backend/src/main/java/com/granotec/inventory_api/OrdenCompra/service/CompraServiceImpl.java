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
        return ordenCompraRepository.findByIsDeletedFalseOrIsDeletedIsNull().stream()
                .map(compraMapperHelper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public CompraResponse actualizarCompra(Integer id, CompraRequest request) {
        OrdenCompra ordenCompra = ordenCompraRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Orden de compra no encontrada"));

        // Validar que no esté eliminada
        if (Boolean.TRUE.equals(ordenCompra.getIsDeleted())) {
            throw new BadRequestException("No se puede actualizar una orden de compra eliminada");
        }

        // Actualizar datos básicos
        Vendor proveedor = vendorRepository.findById(request.getProveedorId())
                .orElseThrow(() -> new BadRequestException("Proveedor no encontrado"));

        Storage almacen = storageRepository.findById(request.getAlmacenId())
                .orElseThrow(() -> new BadRequestException("Almacén no encontrado"));

        ordenCompra.setNumero(request.getNumeroFactura());
        ordenCompra.setProveedor(proveedor);
        ordenCompra.setAlmacen(almacen);
        ordenCompra.setFecha(request.getFecha() != null ? request.getFecha() : ordenCompra.getFecha());

        // Revertir movimientos de Kardex y Stock de los detalles antiguos
        for (DetalleOrdenCompra detalleAntiguo : ordenCompra.getDetalles()) {
            if (detalleAntiguo.getLote() != null) {
                // Revertir el movimiento en Kardex (registrar una salida)
                KardexRequest kardexReversion = new KardexRequest();
                kardexReversion.setAlmacenId(ordenCompra.getAlmacen().getId());
                kardexReversion.setProductoId(detalleAntiguo.getProducto().getId());
                kardexReversion.setLote(detalleAntiguo.getLote().getCodigoLote());
                kardexReversion.setNumeroOp(null);
                kardexReversion.setTipoMovimiento(TipoMovimiento.SALIDA);
                kardexReversion.setTipoOperacion(TypeOperation.ELIMINACION);
                kardexReversion.setReferencia("Reversión de compra " + ordenCompra.getNumero() + " (edición)");
                kardexReversion.setCantidad(detalleAntiguo.getCantidadRecibida());
                kardexReversion.setCostoUnitarioSoles(detalleAntiguo.getPrecioUnitario());
                kardexReversion.setTasaCambio(BigDecimal.valueOf(3.5));
                kardexReversion.setObservacion("Reversión por edición de orden de compra");
                kardexReversion.setUsuarioId(1);
                
                kardexService.registrarMovimientos(kardexReversion);

                // Actualizar o eliminar el StockLote
                StockLote stockLote = stockLoteRepository.findByLoteIdAndAlmacenId(
                        detalleAntiguo.getLote().getId().longValue(), 
                        ordenCompra.getAlmacen().getId()
                ).orElse(null);
                if (stockLote != null) {
                    BigDecimal nuevaCantidad = stockLote.getCantidadDisponible().subtract(detalleAntiguo.getCantidadRecibida());
                    if (nuevaCantidad.compareTo(BigDecimal.ZERO) <= 0) {
                        stockLoteRepository.delete(stockLote);
                    } else {
                        stockLote.setCantidadDisponible(nuevaCantidad);
                        stockLoteRepository.save(stockLote);
                    }
                }
            }
        }

        // Eliminar detalles existentes
        if (ordenCompra.getDetalles() != null && !ordenCompra.getDetalles().isEmpty()) {
            detalleOrdenCompraRepository.deleteAll(ordenCompra.getDetalles());
            ordenCompra.getDetalles().clear();
        }

        // Crear nuevos detalles y registrar en Kardex
        BigDecimal total = BigDecimal.ZERO;
        for (CompraRequest.DetalleCompraDTO detRequest : request.getDetalles()) {
            Product producto = productRepository.findById(detRequest.getProductoId())
                    .orElseThrow(() -> new BadRequestException("Producto no encontrado: " + detRequest.getProductoId()));

            DetalleOrdenCompra detalle = new DetalleOrdenCompra();
            detalle.setOrdenCompra(ordenCompra);
            detalle.setProducto(producto);
            detalle.setCantidadOrdenada(detRequest.getCantidadOrdenada());
            detalle.setCantidadRecibida(detRequest.getCantidadRecibida());
            detalle.setPrecioUnitario(detRequest.getPrecioUnitario());
            detalle.setSubtotal(detRequest.getCantidadOrdenada().multiply(detRequest.getPrecioUnitario()));

            // Gestionar lote si existe
            if (detRequest.getLote() != null && !detRequest.getLote().isBlank()) {
                Lote lote = loteRepository.findByCodigoLote(detRequest.getLote())
                        .orElseGet(() -> {
                            Lote nuevoLote = new Lote();
                            nuevoLote.setCodigoLote(detRequest.getLote());
                            nuevoLote.setProducto(producto);
                            nuevoLote.setCantidadProducida(BigDecimal.ZERO);
                            nuevoLote.setCostoTotal(detRequest.getCantidadRecibida().multiply(detRequest.getPrecioUnitario()));
                            nuevoLote.setCostoUnitario(detRequest.getPrecioUnitario());
                            nuevoLote.setPrecioVentaUnitario(detRequest.getPrecioUnitario());
                            nuevoLote.setEstado("DISPONIBLE");

                            if (detRequest.getFechaProduccion() != null) {
                                nuevoLote.setFechaProduccion(detRequest.getFechaProduccion());
                            }
                            if (detRequest.getFechaVencimiento() != null) {
                                nuevoLote.setFechaVencimiento(detRequest.getFechaVencimiento());
                            }

                            return loteRepository.save(nuevoLote);
                        });
                detalle.setLote(lote);

                // Actualizar o crear StockLote
                StockLote stockLote = stockLoteRepository.findByLoteIdAndAlmacenId(
                        lote.getId().longValue(), 
                        almacen.getId()
                ).orElseGet(() -> {
                            StockLote nuevoStock = new StockLote();
                            nuevoStock.setLote(lote);
                            nuevoStock.setAlmacen(almacen);
                            nuevoStock.setCantidadDisponible(BigDecimal.ZERO);
                            return nuevoStock;
                        });
                
                stockLote.setCantidadDisponible(stockLote.getCantidadDisponible().add(detRequest.getCantidadRecibida()));
                stockLoteRepository.save(stockLote);

                // Registrar entrada en Kardex
                KardexRequest requestK = new KardexRequest();
                requestK.setAlmacenId(request.getAlmacenId());
                requestK.setProductoId(detRequest.getProductoId());
                requestK.setLote(detRequest.getLote());
                requestK.setNumeroOp(null);
                requestK.setTipoMovimiento(TipoMovimiento.ENTRADA);
                requestK.setTipoOperacion(TypeOperation.COMPRA);
                requestK.setReferencia(request.getNumeroFactura());
                requestK.setCantidad(detRequest.getCantidadRecibida());
                requestK.setCostoUnitarioSoles(detRequest.getPrecioUnitario());
                requestK.setTasaCambio(BigDecimal.valueOf(3.5));
                requestK.setObservacion("Actualización de orden de compra");
                requestK.setUsuarioId(1);

                kardexService.registrarMovimientos(requestK);
            }

            ordenCompra.getDetalles().add(detalle);
            total = total.add(detalle.getSubtotal());
        }

        ordenCompra.setTotal(total);
        ordenCompraRepository.save(ordenCompra);

        return compraMapperHelper.toDto(ordenCompra);
    }

    @Override
    @Transactional
    public void eliminarCompra(Integer id) {
        OrdenCompra ordenCompra = ordenCompraRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Orden de compra no encontrada"));

        // Validar que no esté ya eliminada
        if (Boolean.TRUE.equals(ordenCompra.getIsDeleted())) {
            throw new BadRequestException("La orden de compra ya está eliminada");
        }

        // Revertir movimientos de Kardex y Stock antes de eliminar
        for (DetalleOrdenCompra detalle : ordenCompra.getDetalles()) {
            if (detalle.getLote() != null) {
                // Revertir el movimiento en Kardex (registrar una salida)
                KardexRequest kardexReversion = new KardexRequest();
                kardexReversion.setAlmacenId(ordenCompra.getAlmacen().getId());
                kardexReversion.setProductoId(detalle.getProducto().getId());
                kardexReversion.setLote(detalle.getLote().getCodigoLote());
                kardexReversion.setNumeroOp(null);
                kardexReversion.setTipoMovimiento(TipoMovimiento.SALIDA);
                kardexReversion.setTipoOperacion(TypeOperation.ELIMINACION);
                kardexReversion.setReferencia("Eliminación de compra " + ordenCompra.getNumero());
                kardexReversion.setCantidad(detalle.getCantidadRecibida());
                kardexReversion.setCostoUnitarioSoles(detalle.getPrecioUnitario());
                kardexReversion.setTasaCambio(BigDecimal.valueOf(3.5));
                kardexReversion.setObservacion("Reversión por eliminación de orden de compra");
                kardexReversion.setUsuarioId(1);
                
                kardexService.registrarMovimientos(kardexReversion);

                // Actualizar o eliminar el StockLote
                StockLote stockLote = stockLoteRepository.findByLoteIdAndAlmacenId(
                        detalle.getLote().getId().longValue(), 
                        ordenCompra.getAlmacen().getId()
                ).orElse(null);
                
                if (stockLote != null) {
                    BigDecimal nuevaCantidad = stockLote.getCantidadDisponible().subtract(detalle.getCantidadRecibida());
                    if (nuevaCantidad.compareTo(BigDecimal.ZERO) <= 0) {
                        // Si la cantidad es cero o negativa, eliminar el StockLote
                        stockLoteRepository.delete(stockLote);
                    } else {
                        // Si aún queda stock, actualizar la cantidad
                        stockLote.setCantidadDisponible(nuevaCantidad);
                        stockLoteRepository.save(stockLote);
                    }
                }
            }
        }

        // Eliminación lógica de la orden de compra
        ordenCompra.setIsDeleted(Boolean.TRUE);

        // Eliminación lógica de todos los detalles
        if (ordenCompra.getDetalles() != null && !ordenCompra.getDetalles().isEmpty()) {
            ordenCompra.getDetalles().forEach(detalle -> {
                detalle.setIsDeleted(Boolean.TRUE);
            });
        }

        ordenCompraRepository.save(ordenCompra);
    }

}
