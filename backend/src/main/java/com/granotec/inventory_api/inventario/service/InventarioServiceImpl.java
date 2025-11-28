package com.granotec.inventory_api.inventario.service;

import com.granotec.inventory_api.ajustes.service.AjusteService;
import com.granotec.inventory_api.inventario.dto.*;
import com.granotec.inventory_api.product.Product;
import com.granotec.inventory_api.storage.Storage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.granotec.inventory_api.Stock_Almacen.StockAlmacenRepository;
import com.granotec.inventory_api.StockLote.StockLoteRepository;
import com.granotec.inventory_api.Lote.LoteRepository;
import com.granotec.inventory_api.Kardex.KardexRepository;
import com.granotec.inventory_api.product.ProductRepository;
import com.granotec.inventory_api.storage.StorageRepository;
import com.granotec.inventory_api.common.mapper.*;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventarioServiceImpl implements InventarioService {
     private final StockAlmacenRepository stockAlmacenRepository;
     private final StockLoteRepository stockLoteRepository;
     private final LoteRepository loteRepository;
     private final KardexRepository kardexRepository;
     private final ProductRepository productRepository;
     private final StorageRepository storageRepository;
     private final StockAlmacenMapper stockAlmacenMapper;
     private final StockLoteMapper stockLoteMapper;
     private final LoteMapper loteMapper;
     private final KardexMapper kardexMapper;
     private final AjusteService ajusteService;

    @Override
    @Transactional
    public MovimientoInventarioResponse registrarEntrada(MovimientoInventarioRequest request) {
        Storage almacen = storageRepository.findById(request.getAlmacenId())
                .orElseThrow(() -> new IllegalArgumentException("Almacén no encontrado"));
        Product producto = productRepository.findById(request.getProductoId().intValue())
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
        BigDecimal cantidad = request.getCantidad();
        if (cantidad == null || cantidad.compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Cantidad debe ser mayor a cero");
        }
        // StockAlmacen
        var stockAlmacenList = stockAlmacenRepository.findByProductoIdAndAlmacenId(request.getProductoId(), request.getAlmacenId());
        var stockAlmacen = stockAlmacenList.isEmpty() ? null : stockAlmacenList.get(0);
        if (stockAlmacen == null) {
            stockAlmacen = new com.granotec.inventory_api.Stock_Almacen.StockAlmacen();
            stockAlmacen.setAlmacen(almacen);
            stockAlmacen.setProducto(producto);
            stockAlmacen.setCantidad(java.math.BigDecimal.ZERO);
        }
        var stockAnterior = stockAlmacen.getCantidad();
        var nuevoStock = stockAnterior.add(cantidad);
        stockAlmacen.setCantidad(nuevoStock);
        stockAlmacenRepository.save(stockAlmacen);
        // StockLote (si corresponde)
        com.granotec.inventory_api.StockLote.StockLote stockLote = null;
        if (request.getLoteId() != null) {
            var optStockLote = stockLoteRepository.findByLoteIdAndAlmacenId(request.getLoteId(), request.getAlmacenId());
            if (optStockLote.isPresent()) {
                stockLote = optStockLote.get();
                stockLote.setCantidadDisponible(stockLote.getCantidadDisponible().add(cantidad));
            } else {
                // Crear nuevo StockLote
                var lote = loteRepository.findById(request.getLoteId().intValue())
                        .orElseThrow(() -> new IllegalArgumentException("Lote no encontrado"));
                stockLote = new com.granotec.inventory_api.StockLote.StockLote();
                stockLote.setLote(lote);
                stockLote.setAlmacen(almacen);
                stockLote.setCantidadDisponible(cantidad);
            }
            stockLoteRepository.save(stockLote);
        }
        // Registrar en Kardex
        var kardex = new com.granotec.inventory_api.Kardex.Kardex();
        kardex.setAlmacen(almacen);
        kardex.setProducto(producto);
        kardex.setCantidad(cantidad);
        kardex.setTipoMovimiento(com.granotec.inventory_api.common.enums.TipoMovimiento.ENTRADA);
        kardex.setTipoOperacion(com.granotec.inventory_api.common.enums.TypeOperation.OTROS);
        kardex.setStockAnterior(stockAnterior);
        kardex.setStockActual(nuevoStock);
        kardex.setObservacion(request.getObservacion());
        kardex.setFechaMovimiento(java.time.LocalDate.now());
        if (stockLote != null) kardex.setLote(stockLote.getLote());
        kardexRepository.save(kardex);
        // Respuesta
        var response = new MovimientoInventarioResponse();
        response.setIdMovimiento(kardex.getId());
        response.setTipoMovimiento("ENTRADA");
        response.setTipoOperacion("OTROS");
        response.setAlmacenId(almacen.getId());
        response.setProductoId(producto.getId().longValue());
        response.setLoteId(stockLote != null ? stockLote.getLote().getId().longValue() : null);
        response.setCantidad(cantidad);
        response.setFechaMovimiento(java.time.LocalDate.now().toString());
        response.setObservacion(request.getObservacion());
        response.setStockAnterior(stockAnterior);
        response.setStockActual(nuevoStock);
        response.setUsuario(request.getUsuarioId() != null ? request.getUsuarioId().toString() : null);
        return response;
    }

    @Override
    @Transactional
    public MovimientoInventarioResponse registrarSalida(MovimientoInventarioRequest request) {
        var almacen = storageRepository.findById(request.getAlmacenId())
                .orElseThrow(() -> new IllegalArgumentException("Almacén no encontrado"));
        var producto = productRepository.findById(request.getProductoId().intValue())
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
        var cantidad = request.getCantidad();
        if (cantidad == null || cantidad.compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Cantidad debe ser mayor a cero");
        }
        // Lock FIFO de lotes disponibles
        var lotesFIFO = stockLoteRepository.findAvailableByProductoAndAlmacenForUpdate(request.getProductoId(), request.getAlmacenId());
        java.math.BigDecimal restante = cantidad;
        for (var stockLote : lotesFIFO) {
            if (restante.compareTo(java.math.BigDecimal.ZERO) <= 0) break;
            var disponible = stockLote.getCantidadDisponible();
            var aDescontar = disponible.min(restante);
            if (aDescontar.compareTo(java.math.BigDecimal.ZERO) <= 0) continue;
            // Descontar del stock del lote
            stockLote.setCantidadDisponible(disponible.subtract(aDescontar));
            stockLoteRepository.save(stockLote);
            // Registrar kardex por este lote
            var kardexLote = new com.granotec.inventory_api.Kardex.Kardex();
            kardexLote.setAlmacen(almacen);
            kardexLote.setProducto(producto);
            kardexLote.setCantidad(aDescontar);
            kardexLote.setTipoMovimiento(com.granotec.inventory_api.common.enums.TipoMovimiento.SALIDA);
            kardexLote.setTipoOperacion(com.granotec.inventory_api.common.enums.TypeOperation.OTROS);
            kardexLote.setStockAnterior(disponible);
            kardexLote.setStockActual(stockLote.getCantidadDisponible());
            kardexLote.setObservacion(request.getObservacion());
            kardexLote.setFechaMovimiento(java.time.LocalDate.now());
            kardexLote.setLote(stockLote.getLote());
            kardexRepository.save(kardexLote);
            restante = restante.subtract(aDescontar);
        }
        if (restante.compareTo(java.math.BigDecimal.ZERO) > 0) {
            throw new IllegalArgumentException("Stock insuficiente para salida");
        }
        // StockAlmacen
        var stockAlmacenList = stockAlmacenRepository.findByProductoIdAndAlmacenId(request.getProductoId(), request.getAlmacenId());
        var stockAlmacen = stockAlmacenList.isEmpty() ? null : stockAlmacenList.get(0);
        if (stockAlmacen == null || stockAlmacen.getCantidad().compareTo(cantidad) < 0) {
            throw new IllegalArgumentException("Stock insuficiente en almacén");
        }
        var stockAnterior = stockAlmacen.getCantidad();
        var nuevoStock = stockAnterior.subtract(cantidad);
        stockAlmacen.setCantidad(nuevoStock);
        stockAlmacenRepository.save(stockAlmacen);
        // Responder con un único Movimiento representativo
        var response = new MovimientoInventarioResponse();
        response.setIdMovimiento(null);
        response.setTipoMovimiento("SALIDA");
        response.setTipoOperacion("OTROS");
        response.setAlmacenId(almacen.getId());
        response.setProductoId(producto.getId().longValue());
        response.setLoteId(null);
        response.setCantidad(cantidad);
        response.setFechaMovimiento(java.time.LocalDate.now().toString());
        response.setObservacion(request.getObservacion());
        response.setStockAnterior(stockAnterior);
        response.setStockActual(nuevoStock);
        response.setUsuario(request.getUsuarioId() != null ? request.getUsuarioId().toString() : null);
        return response;
    }

    @Override
    @Transactional
    public MovimientoInventarioResponse registrarAjuste(AjusteInventarioRequest request) {
        var ajusteRequest = new com.granotec.inventory_api.ajustes.dto.AjusteRequest();
        ajusteRequest.setAlmacenId(request.getAlmacenId());
        ajusteRequest.setProductoId(request.getProductoId());
        ajusteRequest.setLoteId(request.getLoteId() != null ? request.getLoteId().intValue() : null);
        ajusteRequest.setCantidad(request.getCantidad());
        ajusteRequest.setTipoAjuste(request.getTipoAjuste());
        ajusteRequest.setMotivo(request.getMotivo());
        ajusteRequest.setUsuarioId(request.getUsuarioId());
        var ajusteResponse = ajusteService.registrarAjuste(ajusteRequest);
        var response = new MovimientoInventarioResponse();
        response.setIdMovimiento(ajusteResponse.getIdAjuste());
        response.setTipoMovimiento(ajusteResponse.getTipoAjuste());
        response.setAlmacenId(ajusteResponse.getAlmacenId());
        response.setProductoId(ajusteResponse.getProductoId());
        response.setLoteId(ajusteResponse.getLoteId() != null ? ajusteResponse.getLoteId().longValue() : null);
        response.setCantidad(ajusteResponse.getCantidad());
        response.setFechaMovimiento(ajusteResponse.getFechaAjuste());
        response.setObservacion(ajusteResponse.getMotivo());
        response.setStockAnterior(null);
        response.setStockActual(null);
        response.setUsuario(ajusteResponse.getUsuario());
        return response;
    }

    @Override
    public List<StockAlmacenResponse> obtenerStockPorAlmacen(Long almacenId) {
        var stockList = stockAlmacenRepository.findAll();
        return stockList.stream()
                .filter(s -> s.getAlmacen().getId().equals(almacenId))
                .map(stockAlmacenMapper::toDto)
                .toList();
    }

    @Override
    public List<StockLoteResponse> obtenerStockPorLote(Integer productoId, Long almacenId) {
        var stockLotes = stockLoteRepository.findByLoteProductoIdAndAlmacenId(productoId, almacenId);
        return stockLotes.stream()
                .map(stockLoteMapper::toDto)
                .toList();
    }

    @Override
    public List<StockDisponibleResponse> obtenerStockDisponible(Integer productoId) {
        var stockLotes = stockLoteRepository.findByLoteProductoId(productoId);
        return stockLotes.stream()
                .filter(s -> s.getCantidadDisponible().compareTo(java.math.BigDecimal.ZERO) > 0)
                .map(lote -> {
                    var dto = new StockDisponibleResponse();
                    dto.setProductoId(lote.getLote().getProducto().getId().longValue());
                    dto.setAlmacenId(lote.getAlmacen().getId());
                    dto.setLoteId(lote.getLote().getId().longValue());
                    dto.setCantidadDisponible(lote.getCantidadDisponible());
                    return dto;
                })
                .toList();
    }

    @Override
    @Transactional
    public MovimientoInventarioResponse transferir(TransferenciaRequest request) {
        // Validaciones básicas
        if (request.getAlmacenOrigenId().equals(request.getAlmacenDestinoId())) {
            throw new IllegalArgumentException("El almacén origen y destino no pueden ser iguales");
        }
        var almacenOrigen = storageRepository.findById(request.getAlmacenOrigenId())
                .orElseThrow(() -> new IllegalArgumentException("Almacén origen no encontrado"));
        var almacenDestino = storageRepository.findById(request.getAlmacenDestinoId())
                .orElseThrow(() -> new IllegalArgumentException("Almacén destino no encontrado"));
        var producto = productRepository.findById(request.getProductoId().intValue())
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
        var cantidad = request.getCantidad();
        if (cantidad == null || cantidad.compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Cantidad debe ser mayor a cero");
        }
        // Lock FIFO de lotes disponibles en origen
        var lotesFIFO = stockLoteRepository.findAvailableByProductoAndAlmacenForUpdate(request.getProductoId(), request.getAlmacenOrigenId());
        java.math.BigDecimal restante = cantidad;
        java.util.List<com.granotec.inventory_api.StockLote.StockLote> lotesUsados = new java.util.ArrayList<>();
        for (var stockLote : lotesFIFO) {
            if (restante.compareTo(java.math.BigDecimal.ZERO) <= 0) break;
            var disponible = stockLote.getCantidadDisponible();
            var aTransferir = disponible.min(restante);
            // Descontar en origen
            stockLote.setCantidadDisponible(disponible.subtract(aTransferir));
            stockLoteRepository.save(stockLote);
            // Sumar en destino (StockLote)
            var optDestino = stockLoteRepository.findByLoteIdAndAlmacenId(stockLote.getLote().getId().longValue(), request.getAlmacenDestinoId());
            com.granotec.inventory_api.StockLote.StockLote destinoLote;
            if (optDestino.isPresent()) {
                destinoLote = optDestino.get();
                destinoLote.setCantidadDisponible(destinoLote.getCantidadDisponible().add(aTransferir));
            } else {
                destinoLote = new com.granotec.inventory_api.StockLote.StockLote();
                destinoLote.setLote(stockLote.getLote());
                destinoLote.setAlmacen(almacenDestino);
                destinoLote.setCantidadDisponible(aTransferir);
            }
            stockLoteRepository.save(destinoLote);
            lotesUsados.add(stockLote);
            restante = restante.subtract(aTransferir);
        }
        if (restante.compareTo(java.math.BigDecimal.ZERO) > 0) {
            throw new IllegalArgumentException("Stock insuficiente para transferir");
        }
        // StockAlmacen origen
        var stockAlmacenOrigenList = stockAlmacenRepository.findByProductoIdAndAlmacenId(request.getProductoId(), request.getAlmacenOrigenId());
        var stockAlmacenOrigen = stockAlmacenOrigenList.isEmpty() ? null : stockAlmacenOrigenList.get(0);
        if (stockAlmacenOrigen == null || stockAlmacenOrigen.getCantidad().compareTo(cantidad) < 0) {
            throw new IllegalArgumentException("Stock insuficiente en almacén origen");
        }
        var stockAnteriorOrigen = stockAlmacenOrigen.getCantidad();
        var nuevoStockOrigen = stockAnteriorOrigen.subtract(cantidad);
        stockAlmacenOrigen.setCantidad(nuevoStockOrigen);
        stockAlmacenRepository.save(stockAlmacenOrigen);
        // StockAlmacen destino
        var stockAlmacenDestinoList = stockAlmacenRepository.findByProductoIdAndAlmacenId(request.getProductoId(), request.getAlmacenDestinoId());
        var stockAlmacenDestino = stockAlmacenDestinoList.isEmpty() ? null : stockAlmacenDestinoList.get(0);
        if (stockAlmacenDestino == null) {
            stockAlmacenDestino = new com.granotec.inventory_api.Stock_Almacen.StockAlmacen();
            stockAlmacenDestino.setAlmacen(almacenDestino);
            stockAlmacenDestino.setProducto(producto);
            stockAlmacenDestino.setCantidad(java.math.BigDecimal.ZERO);
        }
        var stockAnteriorDestino = stockAlmacenDestino.getCantidad();
        var nuevoStockDestino = stockAnteriorDestino.add(cantidad);
        stockAlmacenDestino.setCantidad(nuevoStockDestino);
        stockAlmacenRepository.save(stockAlmacenDestino);
        // Registrar en Kardex (salida origen)
        var kardexSalida = new com.granotec.inventory_api.Kardex.Kardex();
        kardexSalida.setAlmacen(almacenOrigen);
        kardexSalida.setProducto(producto);
        kardexSalida.setCantidad(cantidad);
        kardexSalida.setTipoMovimiento(com.granotec.inventory_api.common.enums.TipoMovimiento.SALIDA);
        kardexSalida.setTipoOperacion(com.granotec.inventory_api.common.enums.TypeOperation.TRANSFERENCIA_ENTRE_ALMACENES);
        kardexSalida.setStockAnterior(stockAnteriorOrigen);
        kardexSalida.setStockActual(nuevoStockOrigen);
        kardexSalida.setObservacion("Transferencia a almacén " + almacenDestino.getNombre());
        kardexSalida.setFechaMovimiento(java.time.LocalDate.now());
        kardexRepository.save(kardexSalida);
        // Registrar en Kardex (entrada destino)
        var kardexEntrada = new com.granotec.inventory_api.Kardex.Kardex();
        kardexEntrada.setAlmacen(almacenDestino);
        kardexEntrada.setProducto(producto);
        kardexEntrada.setCantidad(cantidad);
        kardexEntrada.setTipoMovimiento(com.granotec.inventory_api.common.enums.TipoMovimiento.ENTRADA);
        kardexEntrada.setTipoOperacion(com.granotec.inventory_api.common.enums.TypeOperation.TRANSFERENCIA_ENTRE_ALMACENES);
        kardexEntrada.setStockAnterior(stockAnteriorDestino);
        kardexEntrada.setStockActual(nuevoStockDestino);
        kardexEntrada.setObservacion("Transferencia desde almacén " + almacenOrigen.getNombre());
        kardexEntrada.setFechaMovimiento(java.time.LocalDate.now());
        kardexRepository.save(kardexEntrada);
        // Respuesta
        var response = new MovimientoInventarioResponse();
        response.setIdMovimiento(kardexEntrada.getId());
        response.setTipoMovimiento("TRANSFERENCIA");
        response.setTipoOperacion("TRANSFERENCIA_ENTRE_ALMACENES");
        response.setAlmacenId(almacenDestino.getId());
        response.setProductoId(producto.getId().longValue());
        response.setCantidad(cantidad);
        response.setFechaMovimiento(java.time.LocalDate.now().toString());
        response.setObservacion("Transferencia desde almacén " + almacenOrigen.getNombre());
        response.setStockAnterior(stockAnteriorDestino);
        response.setStockActual(nuevoStockDestino);
        response.setUsuario(request.getUsuarioId() != null ? request.getUsuarioId().toString() : null);
        return response;
    }
}
