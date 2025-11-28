package com.granotec.inventory_api.compras.service;

import com.granotec.inventory_api.Kardex.Kardex;
import com.granotec.inventory_api.Lote.Lote;
import com.granotec.inventory_api.Lote.LoteRepository;
import com.granotec.inventory_api.OrdenCompra.OrdenCompra;
import com.granotec.inventory_api.StockLote.StockLote;
import com.granotec.inventory_api.Stock_Almacen.StockAlmacen;
import com.granotec.inventory_api.Stock_Almacen.StockAlmacenRepository;
import com.granotec.inventory_api.common.enums.TipoMovimiento;
import com.granotec.inventory_api.common.enums.TypeOperation;
import com.granotec.inventory_api.common.exception.BadRequestException;
import com.granotec.inventory_api.compras.detalle.DetalleOrdenCompra;
import com.granotec.inventory_api.compras.dto.*;
import com.granotec.inventory_api.product.Product;
import com.granotec.inventory_api.storage.Storage;
import com.granotec.inventory_api.vendor.Vendor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.granotec.inventory_api.OrdenCompra.OrdenCompraRepository;
import com.granotec.inventory_api.StockLote.StockLoteRepository;
import com.granotec.inventory_api.Kardex.KardexRepository;
import com.granotec.inventory_api.product.ProductRepository;
import com.granotec.inventory_api.vendor.VendorRepository;
import com.granotec.inventory_api.storage.StorageRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CompraServiceImpl implements CompraService {
     private final OrdenCompraRepository ordenCompraRepository;
     private final StockLoteRepository stockLoteRepository;
     private final KardexRepository kardexRepository;
     private final VendorRepository vendorRepository;
     private final ProductRepository productRepository;
     private final StorageRepository storageRepository;
     private final LoteRepository loteRepository;
     private final StockAlmacenRepository stockAlmacenRepository;

    @Override
    @Transactional
    public CompraResponse registrarCompra(CompraRequest request) {
        // Validar proveedor y almacén
        Vendor proveedor = vendorRepository.findById(request.getProveedorId())
                .orElseThrow(() -> new BadRequestException("Proveedor no encontrado"));
        Storage almacen = storageRepository.findById(request.getAlmacenId())
                .orElseThrow(() -> new BadRequestException("Almacén no encontrado"));
        // Crear la orden de compra
        OrdenCompra ordenCompra = OrdenCompra.builder()
                .numero(request.getNumero())
                .proveedor(proveedor)
                .almacen(almacen)
                .fecha(LocalDate.now())
                .total(BigDecimal.ZERO)
                .build();
        BigDecimal totalCompra = BigDecimal.ZERO;
        List<DetalleOrdenCompra> detalles = new ArrayList<>();
        for (CompraRequest.DetalleCompraDTO det : request.getDetalles()) {
            Product producto = productRepository.findById(det.getProductoId().intValue())
                    .orElseThrow(() -> new BadRequestException("Producto no encontrado"));

            // Crear lote para la compra
            Lote lote = Lote.builder()
                    .producto(producto)
                    .codigoLote("C" + System.currentTimeMillis() + "-" + producto.getId())
                    .fechaProduccion(LocalDate.now())
                    .cantidadProducida(det.getCantidad())
                    .costoUnitario(det.getPrecioUnitario())
                    .precioVentaUnitario(det.getPrecioUnitario())
                    .costoTotal(det.getCantidad().multiply(det.getPrecioUnitario()))
                    .estado("DISPONIBLE")
                    .build();
            lote = loteRepository.save(lote);
            // Crear o actualizar StockLote
            StockLote stockLote = StockLote.builder()
                    .lote(lote)
                    .almacen(almacen)
                    .cantidadDisponible(det.getCantidad())
                    .build();
            stockLoteRepository.save(stockLote);
            // Actualizar StockAlmacen (este era el faltante para que /inventario/stock devuelva datos)
            var stockAlmacenList = stockAlmacenRepository.findByProductoIdAndAlmacenId(producto.getId(), almacen.getId());
            StockAlmacen stockAlmacen = stockAlmacenList.isEmpty() ? null : stockAlmacenList.get(0); // mantener por compatibilidad
            BigDecimal stockAnterior = stockAlmacen != null ? stockAlmacen.getCantidad() : BigDecimal.ZERO;
            if (stockAlmacen == null) {
                stockAlmacen = StockAlmacen.builder()
                        .almacen(almacen)
                        .producto(producto)
                        .cantidad(BigDecimal.ZERO)
                        .build();
            }
            stockAlmacen.setCantidad(stockAnterior.add(det.getCantidad()));
            stockAlmacenRepository.save(stockAlmacen);
            // Crear detalle de compra
            DetalleOrdenCompra detalle = DetalleOrdenCompra.builder()
                    .ordenCompra(ordenCompra)
                    .producto(producto)
                    .lote(lote)
                    .cantidad(det.getCantidad())
                    .precioUnitario(det.getPrecioUnitario())
                    .build();
            detalle.setSubtotal(det.getCantidad().multiply(det.getPrecioUnitario()));
            detalles.add(detalle);
            totalCompra = totalCompra.add(detalle.getSubtotal());
            // Registrar entrada en Kardex (usar stockAnterior real)
            Kardex kardex = new Kardex();
            kardex.setAlmacen(almacen);
            kardex.setProducto(producto);
            kardex.setCantidad(det.getCantidad());
            kardex.setTipoMovimiento(TipoMovimiento.ENTRADA);
            kardex.setTipoOperacion(TypeOperation.COMPRA);
            kardex.setStockAnterior(stockAnterior);
            kardex.setStockActual(stockAlmacen.getCantidad());
            kardex.setObservacion("Compra - Lote " + lote.getCodigoLote());
            kardex.setFechaMovimiento(LocalDate.now());
            kardex.setLote(lote);
            kardexRepository.save(kardex);
        }
        ordenCompra.setDetalles(detalles);
        ordenCompra.setTotal(totalCompra);
        ordenCompraRepository.save(ordenCompra);
        // Mapear respuesta
        return toDto(ordenCompra);
    }

    @Override
    public CompraResponse obtenerCompra(Integer id) {
        OrdenCompra ordenCompra = ordenCompraRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Compra no encontrada"));
        return toDto(ordenCompra);
    }

    @Override
    public List<CompraResponse> listarCompras() {
        var compras = ordenCompraRepository.findAll();
        var lista = new ArrayList<CompraResponse>();
        for (var ordenCompra : compras) {
            lista.add(toDto(ordenCompra));
        }
        return lista;
    }

    private CompraResponse toDto(OrdenCompra ordenCompra) {
        CompraResponse dto = new CompraResponse();

        dto.setId(ordenCompra.getId());
        dto.setNumero(ordenCompra.getNumero());
        dto.setFecha(LocalDate.now().toString());
        dto.setEstado("REGISTRADO");
        dto.setTotal(ordenCompra.getTotal());

        if(ordenCompra.getProveedor() != null){
            dto.setProveedorId(ordenCompra.getProveedor().getId());
            dto.setProveedorNombre(ordenCompra.getProveedor().getRazonSocial());
        }

        if(ordenCompra.getAlmacen() != null){
            dto.setAlmacenId(ordenCompra.getAlmacen().getId());
            dto.setAlmacenNombre(ordenCompra.getAlmacen().getNombre());
        }

        if(ordenCompra.getDetalles() != null){
            List<CompraResponse.DetalleCompraDTO> detalleCompraDTOS = ordenCompra.getDetalles().stream().map(det -> {
                CompraResponse.DetalleCompraDTO d = new CompraResponse.DetalleCompraDTO();

                d.setCantidad(det.getCantidad());
                d.setPrecioUnitario(det.getPrecioUnitario());
                d.setSubtotal(det.getSubtotal());

                if(det.getProducto() != null){
                    d.setProductoId(det.getProducto().getId().longValue());
                    d.setProductoNombre(det.getProducto().getNombreComercial());
                }

                if(det.getLote() != null){
                    d.setLoteId(det.getLote().getId());
                    d.setCodigoLote(det.getLote().getCodigoLote());
                }

                return d;
            }).toList();

            dto.setDetalles(detalleCompraDTOS);
        }
        return dto;
    }

}
