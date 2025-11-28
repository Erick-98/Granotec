package com.granotec.inventory_api.produccion.service;

import com.granotec.inventory_api.Kardex.Kardex;
import com.granotec.inventory_api.OrdenProduccion.OrdenProduccion;
import com.granotec.inventory_api.StockLote.StockLote;
import com.granotec.inventory_api.common.enums.ProduccionStatus;
import com.granotec.inventory_api.common.enums.TipoMovimiento;
import com.granotec.inventory_api.common.enums.TypeOperation;
import com.granotec.inventory_api.common.exception.BadRequestException;
import com.granotec.inventory_api.produccion.dto.*;
import com.granotec.inventory_api.product.Product;
import com.granotec.inventory_api.common.enums.TypeProduct;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import com.granotec.inventory_api.OrdenProduccion.OrdenProduccionRepository;
import com.granotec.inventory_api.Lote.LoteRepository;
import com.granotec.inventory_api.StockLote.StockLoteRepository;
import com.granotec.inventory_api.Kardex.KardexRepository;
import com.granotec.inventory_api.product.ProductRepository;
import com.granotec.inventory_api.Stock_Almacen.StockAlmacenRepository;
import com.granotec.inventory_api.Stock_Almacen.StockAlmacen;
import com.granotec.inventory_api.storage.StorageRepository;
import com.granotec.inventory_api.storage.Storage;
import com.granotec.inventory_api.OrdenProduccion.OrdenProduccionConsumo;
import com.granotec.inventory_api.OrdenProduccion.OrdenProduccionConsumoRepository;

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
    @Autowired private StockAlmacenRepository stockAlmacenRepository;
    @Autowired private StorageRepository storageRepository;
    @Autowired private OrdenProduccionConsumoRepository ordenProduccionConsumoRepository;

    @Override
    @Transactional
    public OrdenProduccionResponse crearOrden(OrdenProduccionRequest request) {
        Product producto = productRepository.findById(request.getProductoId())
                .orElseThrow(() -> new BadRequestException("Producto no encontrado"));
        // Validar que sea producto terminado
        if (producto.getTipoProducto() != TypeProduct.PRODUCTO_TERMINADO && producto.getTipoProducto() != TypeProduct.PRODUCTO_INTERMEDIO) {
            throw new BadRequestException("Solo se pueden crear órdenes para productos TERMINADOS o INTERMEDIOS");
        }
        // Validar disponibilidad de insumos requeridos antes de crear orden
        if (request.getConsumos() != null && !request.getConsumos().isEmpty()) {
            for (var consumo : request.getConsumos()) {
                var insumo = productRepository.findById(consumo.getInsumoId().intValue())
                        .orElseThrow(() -> new BadRequestException("Insumo " + consumo.getInsumoId() + " no encontrado"));
                if (insumo.getTipoProducto() != TypeProduct.INSUMO) {
                    throw new BadRequestException("El ID " + insumo.getId() + " no corresponde a un producto de tipo INSUMO");
                }
                BigDecimal requerido = consumo.getCantidad();
                if (requerido == null || requerido.compareTo(BigDecimal.ZERO) <= 0) {
                    throw new BadRequestException("Cantidad de insumo debe ser mayor a cero");
                }
                // Obtener stock total disponible del insumo (sumatoria de todos los almacenes)
                BigDecimal totalInsumo = stockLoteRepository.findByLoteProductoId(insumo.getId()).stream()
                        .map(StockLote::getCantidadDisponible)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                if (totalInsumo.compareTo(requerido) < 0) {
                    throw new BadRequestException("Stock insuficiente del insumo ID " + insumo.getId() + ": requerido=" + requerido + ", disponible=" + totalInsumo);
                }
            }
        }
        Storage almacenDestino = storageRepository.findById(request.getAlmacenDestinoId())
                .orElseThrow(() -> new BadRequestException("Almacén destino no encontrado"));
        // crear OrdenProduccion incluyendo almacenDestino
        OrdenProduccion orden = OrdenProduccion.builder()
                .producto(producto)
                .fechaInicio(LocalDate.parse(request.getFechaInicio()))
                .estado(ProduccionStatus.READY)
                .almacenDestino(almacenDestino)
                .build();
        ordenProduccionRepository.save(orden);
        return mapOrdenToResponse(orden, request.getConsumos(), null, null);
    }

    @Override
    @Transactional
    public OrdenProduccionResponse iniciarOrden(Integer ordenId) {
        OrdenProduccion orden = ordenProduccionRepository.findById(ordenId)
                .orElseThrow(() -> new BadRequestException("Orden de producción no encontrada"));
        if (!orden.getEstado().equals(ProduccionStatus.READY)) {
            throw new BadRequestException("Solo se puede iniciar una orden en estado READY");
        }
        orden.setEstado(ProduccionStatus.IN_PRODUCTION);
        ordenProduccionRepository.save(orden);
        return mapOrdenToResponse(orden, null);
    }

    @Override
    @Transactional
    public OrdenProduccionResponse registrarConsumo(ConsumoInsumoRequest request) {
        OrdenProduccion orden = ordenProduccionRepository.findById(request.getOrdenProduccionId())
                .orElseThrow(() -> new BadRequestException("Orden de producción no encontrada"));
        if (!orden.getEstado().equals(ProduccionStatus.IN_PRODUCTION)) {
            throw new BadRequestException("Solo se puede consumir insumos en una orden IN_PRODUCTION");
        }
        // Usar productos de tipo INSUMO en vez de entidad Insumo aislada
        Product insumo = productRepository.findById(request.getInsumoId())
                .orElseThrow(() -> new BadRequestException("Insumo (producto) no encontrado"));
        if (insumo.getTipoProducto() != TypeProduct.INSUMO) {
            throw new BadRequestException("El producto indicado no es de tipo INSUMO");
        }
        BigDecimal cantidad = request.getCantidad();
        if (cantidad == null || cantidad.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Cantidad de insumo debe ser mayor a cero");
        }
        // Obtener lotes FIFO global con lock para este insumo
        var lotesFIFO = stockLoteRepository.findAvailableByProductoForUpdate(insumo.getId());
        BigDecimal restante = cantidad;
        for (StockLote stockLote : lotesFIFO) {
            if (restante.compareTo(BigDecimal.ZERO) <= 0) break;
            BigDecimal disponible = stockLote.getCantidadDisponible();
            BigDecimal aDescontar = disponible.min(restante);
            if (aDescontar.compareTo(BigDecimal.ZERO) <= 0) continue;
            stockLote.setCantidadDisponible(disponible.subtract(aDescontar));
            stockLoteRepository.save(stockLote);
            // Ajustar StockAlmacen del insumo en el almacén del lote
            var stockAList = stockAlmacenRepository.findByProductoIdAndAlmacenId(insumo.getId(), stockLote.getAlmacen().getId());
            StockAlmacen stockAlm = stockAList.isEmpty() ? null : stockAList.get(0);
            if (stockAlm == null || stockAlm.getCantidad().compareTo(aDescontar) < 0) {
                throw new BadRequestException("Stock insuficiente en almacén para insumo (almacen=" + stockLote.getAlmacen().getId() + ")");
            }
            stockAlm.setCantidad(stockAlm.getCantidad().subtract(aDescontar));
            stockAlmacenRepository.save(stockAlm);
            // Kardex
            Kardex kardex = new Kardex();
            kardex.setAlmacen(stockLote.getAlmacen());
            kardex.setProducto(stockLote.getLote().getProducto());
            kardex.setCantidad(aDescontar);
            kardex.setTipoMovimiento(TipoMovimiento.SALIDA);
            kardex.setTipoOperacion(TypeOperation.PRODUCCION);
            kardex.setStockAnterior(disponible);
            kardex.setStockActual(stockLote.getCantidadDisponible());
            kardex.setObservacion("Consumo OP " + orden.getId() + " - Lote " + stockLote.getLote().getCodigoLote());
            kardex.setFechaMovimiento(LocalDate.now());
            kardex.setLote(stockLote.getLote());
            kardexRepository.save(kardex);
            restante = restante.subtract(aDescontar);
        }
        if (restante.compareTo(BigDecimal.ZERO) > 0) {
            throw new BadRequestException("Stock insuficiente de insumo para consumo");
        }
        // Persistir consumo agregado por cada llamada
        var consumoEntity = OrdenProduccionConsumo.builder()
                .ordenProduccion(orden)
                .productoInsumo(insumo)
                .cantidadConsumida(cantidad)
                .build();
        ordenProduccionConsumoRepository.save(consumoEntity);
        return mapOrdenToResponse(orden, null, null, null);
    }

    @Override
    @Transactional
    public OrdenProduccionResponse finalizarOrden(FinalizarProduccionRequest request) {
        var orden = ordenProduccionRepository.findById(request.getOrdenProduccionId())
                .orElseThrow(() -> new IllegalArgumentException("Orden de producción no encontrada"));
        if (!orden.getEstado().equals(ProduccionStatus.IN_PRODUCTION)) {
            throw new IllegalStateException("Solo se puede finalizar una orden IN_PRODUCTION");
        }
        orden.setEstado(ProduccionStatus.TERMINADA);
        orden.setFechaFin(LocalDate.now());
        ordenProduccionRepository.save(orden);
        var producto = orden.getProducto();
        String codigoLote = request.getLoteCodigoManual() != null ? request.getLoteCodigoManual() : (request.getAlmacenDestinoId() != null ? "P"+orden.getId()+"-"+System.currentTimeMillis() : "P"+System.currentTimeMillis());
        var lote = new com.granotec.inventory_api.Lote.Lote();
        lote.setOrdenProduccion(orden);
        lote.setProducto(producto);
        lote.setCodigoLote(codigoLote);
        lote.setFechaProduccion(LocalDate.now());
        lote.setCantidadProducida(request.getCantidadProducida());
        lote.setCostoTotal(request.getCostoTotal());
        lote.setCostoUnitario(request.getCantidadProducida().compareTo(BigDecimal.ZERO) > 0 ?
                request.getCostoTotal().divide(request.getCantidadProducida(), java.math.MathContext.DECIMAL64) : BigDecimal.ZERO);
        lote.setPrecioVentaUnitario(lote.getCostoUnitario());
        lote.setEstado("DISPONIBLE");
        loteRepository.save(lote);
        // Crear StockLote y StockAlmacen inmediatamente en almacen destino
        Long almacenIdFinal = request.getAlmacenDestinoId() != null ? request.getAlmacenDestinoId() : orden.getAlmacenDestino().getId();
        Storage almacenDestino = storageRepository.findById(almacenIdFinal)
                .orElseThrow(() -> new BadRequestException("Almacén destino no encontrado"));
        // StockLote
        var stockLote = new com.granotec.inventory_api.StockLote.StockLote();
        stockLote.setLote(lote);
        stockLote.setAlmacen(almacenDestino);
        stockLote.setCantidadDisponible(request.getCantidadProducida());
        stockLoteRepository.save(stockLote);
        // StockAlmacen
        var stockAlmacenList = stockAlmacenRepository.findByProductoIdAndAlmacenId(producto.getId(), almacenDestino.getId());
        StockAlmacen stockAlmacen = stockAlmacenList.isEmpty() ? null : stockAlmacenList.get(0);
        if (stockAlmacen == null) {
            stockAlmacen = StockAlmacen.builder().almacen(almacenDestino).producto(producto).cantidad(BigDecimal.ZERO).build();
        }
        BigDecimal stockAnterior = stockAlmacen.getCantidad();
        stockAlmacen.setCantidad(stockAnterior.add(request.getCantidadProducida()));
        stockAlmacenRepository.save(stockAlmacen);
        // Kardex ENTRADA producción
        Kardex kardex = new Kardex();
        kardex.setAlmacen(almacenDestino);
        kardex.setProducto(producto);
        kardex.setCantidad(request.getCantidadProducida());
        kardex.setTipoMovimiento(TipoMovimiento.ENTRADA);
        kardex.setTipoOperacion(TypeOperation.PRODUCCION);
        kardex.setStockAnterior(stockAnterior);
        kardex.setStockActual(stockAlmacen.getCantidad());
        kardex.setObservacion("Producción OP " + orden.getId() + " lote=" + codigoLote);
        kardex.setFechaMovimiento(LocalDate.now());
        kardex.setLote(lote);
        kardexRepository.save(kardex);
        return mapOrdenToResponse(orden, null, request.getCantidadProducida(), codigoLote);
    }

    @Override
    public List<OrdenProduccionResponse> listarOrdenes() {
        return ordenProduccionRepository.findAll().stream()
                .map(o -> mapOrdenToResponse(o, null))
                .toList();
    }

    @Override
    public OrdenProduccionResponse obtenerOrden(Integer ordenId) {
        var orden = ordenProduccionRepository.findById(ordenId)
                .orElseThrow(() -> new BadRequestException("Orden de producción no encontrada"));
        return mapOrdenToResponse(orden, null);
    }

    private OrdenProduccionResponse mapOrdenToResponse(OrdenProduccion orden, List<OrdenProduccionRequest.ConsumoInsumoDTO> consumos) {
        return mapOrdenToResponse(orden, consumos, null, null);
    }

    private OrdenProduccionResponse mapOrdenToResponse(OrdenProduccion orden, List<OrdenProduccionRequest.ConsumoInsumoDTO> consumos, BigDecimal cantidadProducidaFinal, String codigoLote) {
        OrdenProduccionResponse response = new OrdenProduccionResponse();
        response.setId(orden.getId());
        response.setProductoId(orden.getProducto().getId().longValue());
        response.setProductoNombre(orden.getProducto().getNombreComercial());
        response.setFechaInicio(orden.getFechaInicio() != null ? orden.getFechaInicio().toString() : null);
        response.setFechaFin(orden.getFechaFin() != null ? orden.getFechaFin().toString() : null);
        response.setEstado(orden.getEstado().name());
        response.setCantidadProducida(cantidadProducidaFinal);
        response.setAlmacenDestinoId(orden.getAlmacenDestino() != null ? orden.getAlmacenDestino().getId() : null);
        response.setCodigoLoteGenerado(codigoLote);
        if (consumos != null) {
            response.setConsumos(consumos.stream().map(c -> {
                OrdenProduccionResponse.ConsumoInsumoDTO dto = new OrdenProduccionResponse.ConsumoInsumoDTO();
                dto.setInsumoId(c.getInsumoId());
                var insumoOpt = productRepository.findById(c.getInsumoId().intValue());
                dto.setInsumoNombre(insumoOpt.map(Product::getNombreComercial).orElse(null));
                dto.setCantidad(c.getCantidad());
                return dto;
            }).toList());
        } else {
            // Cargar consumos reales persistidos
            var consumosPersistidos = ordenProduccionConsumoRepository.findByOrdenProduccionId(orden.getId());
            response.setConsumos(consumosPersistidos.stream().map(cp -> {
                OrdenProduccionResponse.ConsumoInsumoDTO dto = new OrdenProduccionResponse.ConsumoInsumoDTO();
                dto.setInsumoId(cp.getProductoInsumo().getId().longValue());
                dto.setInsumoNombre(cp.getProductoInsumo().getNombreComercial());
                dto.setCantidad(cp.getCantidadConsumida());
                return dto;
            }).toList());
        }
        return response;
    }
}
