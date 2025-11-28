package com.granotec.inventory_api.ventas.service;

import com.granotec.inventory_api.ventas.dto.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import com.granotec.inventory_api.OrdenVenta.OrdenVentaRepository;
import com.granotec.inventory_api.StockLote.StockLoteRepository;
import com.granotec.inventory_api.Kardex.KardexRepository;
import com.granotec.inventory_api.product.ProductRepository;
import com.granotec.inventory_api.customer.CustomerRepository;
import com.granotec.inventory_api.salesperson.SalespersonRepository;
import com.granotec.inventory_api.storage.StorageRepository;
import com.granotec.inventory_api.common.mapper.VentaMapper;
import java.util.List;

@Service
public class VentaServiceImpl implements VentaService {
    @Autowired private OrdenVentaRepository ordenVentaRepository;
    @Autowired private StockLoteRepository stockLoteRepository;
    @Autowired private KardexRepository kardexRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private CustomerRepository customerRepository;
    @Autowired private SalespersonRepository salespersonRepository;
    @Autowired private StorageRepository storageRepository;
    @Autowired private VentaMapper ventaMapper;

    @Override
    @Transactional
    public VentaResponse crearVenta(VentaRequest request) {
        // Validar cliente, vendedor y almacén
        var cliente = customerRepository.findById(request.getClienteId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));
        var vendedor = salespersonRepository.findById(request.getVendedorId().intValue())
                .orElseThrow(() -> new IllegalArgumentException("Vendedor no encontrado"));
        var almacen = storageRepository.findById(request.getAlmacenId())
                .orElseThrow(() -> new IllegalArgumentException("Almacén no encontrado"));
        // Crear la orden de venta
        var ordenVenta = com.granotec.inventory_api.OrdenVenta.OrdenVenta.builder()
                .cliente(cliente)
                .vendedor(vendedor)
                .almacen(almacen)
                .fecha(java.time.LocalDate.now())
                .estado(com.granotec.inventory_api.common.enums.EstadoOrden_Venta.PENDIENTE)
                .total(java.math.BigDecimal.ZERO)
                .build();
        java.math.BigDecimal totalVenta = java.math.BigDecimal.ZERO;
        java.util.List<com.granotec.inventory_api.OrdenVenta.detalles.DetalleOrdenVenta> detalles = new java.util.ArrayList<>();
        for (var det : request.getDetalles()) {
            var producto = productRepository.findById(det.getProductoId().intValue())
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
            // Validar que el producto no esté bloqueado para venta
            if (Boolean.TRUE.equals(producto.getIsLocked())) {
                throw new IllegalStateException("No se puede vender un producto bloqueado o en producción");
            }
            // Buscar lotes disponibles FIFO (no dañados, estado DISPONIBLE)
            var lotesFIFO = stockLoteRepository.findAvailableByProductoAndAlmacenForUpdate(det.getProductoId(), request.getAlmacenId());
            java.math.BigDecimal restante = det.getCantidad();
            for (var stockLote : lotesFIFO) {
                if (restante.compareTo(java.math.BigDecimal.ZERO) <= 0) break;
                // No permitir ventas de lotes en almacén de dañados
                if (stockLote.getAlmacen().getNombre().toLowerCase().contains("dañado")) continue;
                var disponible = stockLote.getCantidadDisponible();
                var aVender = disponible.min(restante);
                if (aVender.compareTo(java.math.BigDecimal.ZERO) <= 0) continue;
                // Descontar stock
                stockLote.setCantidadDisponible(disponible.subtract(aVender));
                stockLoteRepository.save(stockLote);
                // Crear detalle de venta
                var detalle = com.granotec.inventory_api.OrdenVenta.detalles.DetalleOrdenVenta.builder()
                        .ordenVenta(ordenVenta)
                        .producto(producto)
                        .lote(stockLote.getLote())
                        .cantidad(aVender)
                        .precioUnitario(stockLote.getLote().getPrecioVentaUnitario())
                        .build();
                // Calcular subtotal manualmente
                detalle.setSubtotal(detalle.getCantidad().multiply(detalle.getPrecioUnitario()));
                detalles.add(detalle);
                totalVenta = totalVenta.add(detalle.getSubtotal());
                // Registrar salida en Kardex
                var kardex = new com.granotec.inventory_api.Kardex.Kardex();
                kardex.setAlmacen(stockLote.getAlmacen());
                kardex.setProducto(producto);
                kardex.setCantidad(aVender);
                kardex.setTipoMovimiento(com.granotec.inventory_api.common.enums.TipoMovimiento.SALIDA);
                kardex.setTipoOperacion(com.granotec.inventory_api.common.enums.TypeOperation.VENTA);
                kardex.setStockAnterior(disponible);
                kardex.setStockActual(stockLote.getCantidadDisponible());
                kardex.setObservacion("Venta - Lote " + stockLote.getLote().getCodigoLote());
                kardex.setFechaMovimiento(java.time.LocalDate.now());
                kardex.setLote(stockLote.getLote());
                kardexRepository.save(kardex);
                restante = restante.subtract(aVender);
            }
            if (restante.compareTo(java.math.BigDecimal.ZERO) > 0) {
                throw new IllegalArgumentException("Stock insuficiente para el producto " + producto.getNombreComercial());
            }
        }
        ordenVenta.setDetalles(detalles);
        ordenVenta.setTotal(totalVenta);
        ordenVenta.setEstado(com.granotec.inventory_api.common.enums.EstadoOrden_Venta.PROCESADA);
        ordenVentaRepository.save(ordenVenta);
        // Mapear respuesta
        var response = new VentaResponse();
        response.setId(ordenVenta.getId());
        response.setNumero(ordenVenta.getNumero());
        response.setClienteId(cliente.getId());
        response.setClienteNombre(cliente.getRazonSocial() != null ? cliente.getRazonSocial() : cliente.getName());
        response.setVendedorId(vendedor.getId().longValue());
        response.setVendedorNombre(vendedor.getName());
        response.setAlmacenId(almacen.getId());
        response.setAlmacenNombre(almacen.getNombre());
        response.setFecha(ordenVenta.getFecha().toString());
        response.setEstado(ordenVenta.getEstado().name());
        response.setTotal(totalVenta);
        response.setObservaciones(ordenVenta.getObservaciones());
        var detallesResp = new java.util.ArrayList<VentaResponse.DetalleVentaDTO>();
        for (var d : detalles) {
            var dto = new VentaResponse.DetalleVentaDTO();
            dto.setProductoId(d.getProducto().getId().longValue());
            dto.setProductoNombre(d.getProducto().getNombreComercial());
            dto.setLoteId(d.getLote().getId());
            dto.setCodigoLote(d.getLote().getCodigoLote());
            dto.setCantidad(d.getCantidad());
            dto.setPrecioUnitario(d.getPrecioUnitario());
            dto.setSubtotal(d.getSubtotal());
            detallesResp.add(dto);
        }
        response.setDetalles(detallesResp);
        return response;
    }

    @Override
    public VentaResponse obtenerVenta(Integer id) {
        var ordenVenta = ordenVentaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Venta no encontrada"));
        var response = new VentaResponse();
        response.setId(ordenVenta.getId());
        response.setNumero(ordenVenta.getNumero());
        response.setClienteId(ordenVenta.getCliente().getId());
        response.setClienteNombre(ordenVenta.getCliente().getRazonSocial() != null ? ordenVenta.getCliente().getRazonSocial() : ordenVenta.getCliente().getName());
        response.setVendedorId(ordenVenta.getVendedor().getId().longValue());
        response.setVendedorNombre(ordenVenta.getVendedor().getName());
        response.setAlmacenId(ordenVenta.getAlmacen().getId());
        response.setAlmacenNombre(ordenVenta.getAlmacen().getNombre());
        response.setFecha(ordenVenta.getFecha() != null ? ordenVenta.getFecha().toString() : null);
        response.setEstado(ordenVenta.getEstado().name());
        response.setTotal(ordenVenta.getTotal());
        response.setObservaciones(ordenVenta.getObservaciones());
        var detallesResp = new java.util.ArrayList<VentaResponse.DetalleVentaDTO>();
        for (var d : ordenVenta.getDetalles()) {
            var dto = new VentaResponse.DetalleVentaDTO();
            dto.setProductoId(d.getProducto().getId().longValue());
            dto.setProductoNombre(d.getProducto().getNombreComercial());
            dto.setLoteId(d.getLote().getId());
            dto.setCodigoLote(d.getLote().getCodigoLote());
            dto.setCantidad(d.getCantidad());
            dto.setPrecioUnitario(d.getPrecioUnitario());
            dto.setSubtotal(d.getSubtotal());
            detallesResp.add(dto);
        }
        response.setDetalles(detallesResp);
        return response;
    }

    @Override
    public List<VentaResponse> listarVentas() {
        var ventas = ordenVentaRepository.findAll();
        var lista = new java.util.ArrayList<VentaResponse>();
        for (var ordenVenta : ventas) {
            var response = new VentaResponse();
            response.setId(ordenVenta.getId());
            response.setNumero(ordenVenta.getNumero());
            response.setClienteId(ordenVenta.getCliente().getId());
            response.setClienteNombre(ordenVenta.getCliente().getRazonSocial() != null ? ordenVenta.getCliente().getRazonSocial() : ordenVenta.getCliente().getName());
            response.setVendedorId(ordenVenta.getVendedor().getId().longValue());
            response.setVendedorNombre(ordenVenta.getVendedor().getName());
            response.setAlmacenId(ordenVenta.getAlmacen().getId());
            response.setAlmacenNombre(ordenVenta.getAlmacen().getNombre());
            response.setFecha(ordenVenta.getFecha() != null ? ordenVenta.getFecha().toString() : null);
            response.setEstado(ordenVenta.getEstado().name());
            response.setTotal(ordenVenta.getTotal());
            response.setObservaciones(ordenVenta.getObservaciones());
            var detallesResp = new java.util.ArrayList<VentaResponse.DetalleVentaDTO>();
            for (var d : ordenVenta.getDetalles()) {
                var dto = new VentaResponse.DetalleVentaDTO();
                dto.setProductoId(d.getProducto().getId().longValue());
                dto.setProductoNombre(d.getProducto().getNombreComercial());
                dto.setLoteId(d.getLote().getId());
                dto.setCodigoLote(d.getLote().getCodigoLote());
                dto.setCantidad(d.getCantidad());
                dto.setPrecioUnitario(d.getPrecioUnitario());
                dto.setSubtotal(d.getSubtotal());
                detallesResp.add(dto);
            }
            response.setDetalles(detallesResp);
            lista.add(response);
        }
        return lista;
    }
}
