package com.granotec.inventory_api.produccion.service;

import com.granotec.inventory_api.Kardex.Kardex;
import com.granotec.inventory_api.OrdenProduccion.OrdenProduccion;
import com.granotec.inventory_api.StockLote.StockLote;
import com.granotec.inventory_api.common.enums.ProducciónStatus;
import com.granotec.inventory_api.common.enums.TipoMovimiento;
import com.granotec.inventory_api.common.enums.TypeOperation;
import com.granotec.inventory_api.common.exception.BadRequestException;
import com.granotec.inventory_api.produccion.dto.*;
import com.granotec.inventory_api.product.Product;
import jakarta.validation.constraints.Null;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import com.granotec.inventory_api.OrdenProduccion.OrdenProduccionRepository;
import com.granotec.inventory_api.Lote.LoteRepository;
import com.granotec.inventory_api.StockLote.StockLoteRepository;
import com.granotec.inventory_api.Kardex.KardexRepository;
import com.granotec.inventory_api.product.ProductRepository;
import com.granotec.inventory_api.Insumo.InsumoRepository;
import com.granotec.inventory_api.common.mapper.OrdenProduccionMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class ProduccionServiceImpl implements ProduccionService {
    @Autowired private OrdenProduccionRepository ordenProduccionRepository;
    @Autowired private LoteRepository loteRepository;
    @Autowired private StockLoteRepository stockLoteRepository;
    @Autowired private KardexRepository kardexRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private InsumoRepository insumoRepository;
    @Autowired private OrdenProduccionMapper ordenProduccionMapper;

    @Override
    @Transactional
    public OrdenProduccionResponse crearOrden(OrdenProduccionRequest request) {
        Product producto = productRepository.findById(request.getProductoId().intValue())
                .orElseThrow(() -> new BadRequestException("Producto no encontrado"));
        // Validar que sea producto terminado (TypeProduct TERMINADO) si aplica
        // if (producto.getTipoProducto() != TypeProduct.TERMINADO) throw new BadRequestException("El producto debe ser de tipo TERMINADO");
        // Validar insumos si vienen en request.consumos
        if (request.getConsumos() != null && !request.getConsumos().isEmpty()) {
            for (var consumo : request.getConsumos()) {
                var insumo = productRepository.findById(consumo.getInsumoId().intValue())
                        .orElseThrow(() -> new BadRequestException("Insumo " + consumo.getInsumoId() + " no encontrado"));
                var requerido = consumo.getCantidad();
                if (requerido == null || requerido.compareTo(BigDecimal.ZERO) <= 0) {
                    throw new BadRequestException("Cantidad de insumo debe ser mayor a cero");
                }
                // Obtener stock total disponible del insumo (sumatoria en todos los almacenes)
                var totalInsumo = stockLoteRepository.findByLoteProductoId(insumo.getId()).stream()
                        .map(sl -> sl.getCantidadDisponible())
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                if (totalInsumo.compareTo(requerido) < 0) {
                    throw new BadRequestException("Stock insuficiente del insumo ID " + insumo.getId() + ": requerido=" + requerido + ", disponible=" + totalInsumo);
                }
            }
        }
        OrdenProduccion orden = OrdenProduccion.builder()
                .producto(producto)
                .fechaInicio(LocalDate.parse(request.getFechaInicio()))
                .estado(ProducciónStatus.READY)
                .build();
        ordenProduccionRepository.save(orden);
        OrdenProduccionResponse response = new OrdenProduccionResponse();
        response.setId(orden.getId());
        response.setProductoId(producto.getId().longValue());
        response.setProductoNombre(producto.getNombreComercial());
        response.setFechaInicio(orden.getFechaInicio().toString());
        response.setEstado(orden.getEstado().name());
        response.setCantidadProducida(null);
        response.setConsumos(request.getConsumos() != null ? request.getConsumos().stream().map(c -> {
            var dto = new OrdenProduccionResponse.ConsumoInsumoDTO();
            dto.setInsumoId(c.getInsumoId());;
            dto.setCantidad(c.getCantidad());
            return dto;
        }).toList() : List.of());
        return response;
    }

    @Override
    @Transactional
    public OrdenProduccionResponse iniciarOrden(Integer ordenId) {
        OrdenProduccion orden = ordenProduccionRepository.findById(ordenId)
                .orElseThrow(() -> new BadRequestException("Orden de producción no encontrada"));
        if (!orden.getEstado().equals(ProducciónStatus.READY)) {
            throw new BadRequestException("Solo se puede iniciar una orden en estado READY");
        }
        orden.setEstado(ProducciónStatus.IN_PRODUCTION);
        ordenProduccionRepository.save(orden);
        OrdenProduccionResponse response = new OrdenProduccionResponse();
        response.setId(orden.getId());
        response.setProductoId(orden.getProducto().getId().longValue());
        response.setProductoNombre(orden.getProducto().getNombreComercial());
        response.setFechaInicio(orden.getFechaInicio().toString());
        response.setEstado(orden.getEstado().name());
        response.setCantidadProducida(null);
        response.setConsumos(List.of());
        return response;
    }

    @Override
    @Transactional
    public OrdenProduccionResponse registrarConsumo(ConsumoInsumoRequest request) {
        // Validar orden
        OrdenProduccion orden = ordenProduccionRepository.findById(request.getOrdenProduccionId())
                .orElseThrow(() -> new BadRequestException("Orden de producción no encontrada"));
        if (!orden.getEstado().equals(ProducciónStatus.IN_PRODUCTION)) {
            throw new BadRequestException("Solo se puede consumir insumos en una orden EN_PRODUCCION");
        }
        // Validar insumo
        var insumo = insumoRepository.findById(request.getInsumoId().intValue())
                .orElseThrow(() -> new BadRequestException("Insumo no encontrado"));
        BigDecimal cantidad = request.getCantidad();
        if (cantidad == null || cantidad.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Cantidad de insumo debe ser mayor a cero");
        }
        // Descontar stock de insumo (FIFO por lotes disponibles)
        var lotesFIFO = stockLoteRepository.findAvailableByProductoAndAlmacenForUpdate(request.getInsumoId(), null);
        BigDecimal restante = cantidad;
        for (StockLote stockLote : lotesFIFO) {
            if (restante.compareTo(BigDecimal.ZERO) <= 0) break;
            BigDecimal disponible = stockLote.getCantidadDisponible();
            BigDecimal aDescontar = disponible.min(restante);
            stockLote.setCantidadDisponible(disponible.subtract(aDescontar));
            stockLoteRepository.save(stockLote);
            // Registrar en Kardex
            Kardex kardex = new Kardex();
            kardex.setAlmacen(stockLote.getAlmacen());
            // El Kardex guarda el producto relacionado al lote afectado (puede ser insumo)
            kardex.setProducto(stockLote.getLote().getProducto());
            kardex.setCantidad(aDescontar);
            kardex.setTipoMovimiento(TipoMovimiento.SALIDA);
            kardex.setTipoOperacion(TypeOperation.OTROS);
            kardex.setStockAnterior(disponible);
            kardex.setStockActual(stockLote.getCantidadDisponible());
            kardex.setObservacion("Consumo para OP " + orden.getId());
            kardex.setFechaMovimiento(LocalDate.now());
            kardex.setLote(stockLote.getLote());
            kardexRepository.save(kardex);
            restante = restante.subtract(aDescontar);
        }
        if (restante.compareTo(BigDecimal.ZERO) > 0) {
            throw new BadRequestException("Stock insuficiente de insumo para consumo");
        }
        // Responder con estado actualizado de la orden
        OrdenProduccionResponse response = new OrdenProduccionResponse();
        response.setId(orden.getId());
        response.setProductoId(orden.getProducto().getId().longValue());
        response.setProductoNombre(orden.getProducto().getNombreComercial());
        response.setFechaInicio(orden.getFechaInicio().toString());
        response.setEstado(orden.getEstado().name());
        response.setCantidadProducida(null);
        response.setConsumos(List.of()); // Mejorar: listar consumos reales si se requiere
        return response;
    }

    @Override
    @Transactional
    public OrdenProduccionResponse finalizarOrden(FinalizarProduccionRequest request) {
        // Validar orden
        var orden = ordenProduccionRepository.findById(request.getOrdenProduccionId())
                .orElseThrow(() -> new IllegalArgumentException("Orden de producción no encontrada"));
        if (!orden.getEstado().equals(com.granotec.inventory_api.common.enums.ProducciónStatus.IN_PRODUCTION)) {
            throw new IllegalStateException("Solo se puede finalizar una orden EN_PRODUCCION");
        }
        // Cambiar estado a TERMINADA y registrar fecha fin
        orden.setEstado(com.granotec.inventory_api.common.enums.ProducciónStatus.READY); // Si tienes un estado TERMINADA, cámbialo aquí
        orden.setFechaFin(java.time.LocalDate.now());
        ordenProduccionRepository.save(orden);
        // Generar lote del producto
        var producto = orden.getProducto();
        var lote = new com.granotec.inventory_api.Lote.Lote();
        lote.setOrdenProduccion(orden);
        lote.setProducto(producto);
        lote.setCodigoLote("L" + System.currentTimeMillis());
        lote.setFechaProduccion(java.time.LocalDate.now());
        lote.setCantidadProducida(request.getCantidadProducida());
        lote.setCostoTotal(request.getCostoTotal());
        lote.setCostoUnitario(request.getCantidadProducida().compareTo(java.math.BigDecimal.ZERO) > 0 ? request.getCostoTotal().divide(request.getCantidadProducida(), java.math.MathContext.DECIMAL64) : java.math.BigDecimal.ZERO);
        lote.setPrecioVentaUnitario(request.getCantidadProducida().compareTo(java.math.BigDecimal.ZERO) > 0 ? request.getCostoTotal().divide(request.getCantidadProducida(), java.math.MathContext.DECIMAL64) : java.math.BigDecimal.ZERO);
        lote.setEstado("DISPONIBLE");
        loteRepository.save(lote);
        // Nota: no se crea StockLote ni Kardex aquí porque el almacen destino no se especifica en la OP.
        // El stock inicial del lote debe registrarse mediante un movimiento de entrada (inventario.registrarEntrada)
        // para asegurar que la relación con el almacén sea consistente y cumplir constraints NOT NULL
        // Responder con datos finales
        var response = new OrdenProduccionResponse();
        response.setId(orden.getId());
        response.setProductoId(producto.getId().longValue());
        response.setProductoNombre(producto.getNombreComercial());
        response.setFechaInicio(orden.getFechaInicio().toString());
        response.setFechaFin(orden.getFechaFin() != null ? orden.getFechaFin().toString() : null);
        response.setEstado(orden.getEstado().name());
        response.setCantidadProducida(request.getCantidadProducida());
        response.setConsumos(List.of()); // Mejorar: listar consumos reales si se requiere
        return response;
    }

    @Override
    public List<OrdenProduccionResponse> listarOrdenes() {
        // TODO: Implementar lógica para listar órdenes
        return null;
    }

    @Override
    public OrdenProduccionResponse obtenerOrden(Integer ordenId) {
        // TODO: Implementar lógica para obtener detalle de orden
        return null;
    }
}
