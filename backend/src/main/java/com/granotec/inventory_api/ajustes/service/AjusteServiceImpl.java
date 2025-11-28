package com.granotec.inventory_api.ajustes.service;

import com.granotec.inventory_api.Kardex.Kardex;
import com.granotec.inventory_api.Kardex.KardexRepository;
import com.granotec.inventory_api.StockLote.StockLote;
import com.granotec.inventory_api.StockLote.StockLoteRepository;
import com.granotec.inventory_api.Stock_Almacen.StockAlmacen;
import com.granotec.inventory_api.Stock_Almacen.StockAlmacenRepository;
import com.granotec.inventory_api.product.Product;
import com.granotec.inventory_api.product.ProductRepository;
import com.granotec.inventory_api.storage.Storage;
import com.granotec.inventory_api.storage.StorageRepository;
import com.granotec.inventory_api.ajustes.dto.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class AjusteServiceImpl implements AjusteService {
    @Autowired
    private StorageRepository storageRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private StockLoteRepository stockLoteRepository;
    @Autowired
    private StockAlmacenRepository stockAlmacenRepository;
    @Autowired
    private KardexRepository kardexRepository;

    @Override
    @Transactional
    public AjusteResponse registrarAjuste(AjusteRequest request) {
        // Validar existencia de almacén y producto
        Storage almacen = storageRepository.findById(request.getAlmacenId())
            .orElseThrow(() -> new IllegalArgumentException("Almacén no encontrado"));
        Product producto = productRepository.findById(request.getProductoId().intValue())
            .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
        BigDecimal cantidad = request.getCantidad();
        if (cantidad == null || cantidad.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Cantidad debe ser mayor a cero");
        }
        // Ajuste por lote si corresponde
        StockLote stockLote = null;
        if (request.getLoteId() != null) {
            stockLote = stockLoteRepository.findById(request.getLoteId())
                .orElseThrow(() -> new IllegalArgumentException("Lote no encontrado"));
            if (!stockLote.getAlmacen().getId().equals(request.getAlmacenId())) {
                throw new IllegalArgumentException("El lote no pertenece al almacén indicado");
            }
        }
        // Ajuste de stock en StockAlmacen
        List<StockAlmacen> stockAlmacenList = stockAlmacenRepository.findByProductoIdAndAlmacenId(
            request.getProductoId(), request.getAlmacenId());
        StockAlmacen stockAlmacen = stockAlmacenList.isEmpty() ? null : stockAlmacenList.get(0);
        if (stockAlmacen == null) {
            if (request.getTipoAjuste().equalsIgnoreCase("NEGATIVO")) {
                throw new IllegalArgumentException("No hay stock para ajustar negativamente");
            }
            stockAlmacen = new StockAlmacen();
            stockAlmacen.setAlmacen(almacen);
            stockAlmacen.setProducto(producto);
            stockAlmacen.setCantidad(BigDecimal.ZERO);
        }
        BigDecimal stockAnterior = stockAlmacen.getCantidad();
        BigDecimal nuevoStock;
        if (request.getTipoAjuste().equalsIgnoreCase("POSITIVO")) {
            nuevoStock = stockAnterior.add(cantidad);
        } else if (request.getTipoAjuste().equalsIgnoreCase("NEGATIVO")) {
            if (stockAnterior.compareTo(cantidad) < 0) {
                throw new IllegalArgumentException("Stock insuficiente para ajuste negativo");
            }
            nuevoStock = stockAnterior.subtract(cantidad);
        } else {
            throw new IllegalArgumentException("Tipo de ajuste inválido");
        }
        stockAlmacen.setCantidad(nuevoStock);
        stockAlmacenRepository.save(stockAlmacen);
        // Ajuste de stock en StockLote si corresponde
        if (stockLote != null) {
            BigDecimal stockLoteAnterior = stockLote.getCantidadDisponible();
            BigDecimal nuevoStockLote;
            if (request.getTipoAjuste().equalsIgnoreCase("POSITIVO")) {
                nuevoStockLote = stockLoteAnterior.add(cantidad);
            } else {
                if (stockLoteAnterior.compareTo(cantidad) < 0) {
                    throw new IllegalArgumentException("Stock de lote insuficiente para ajuste negativo");
                }
                nuevoStockLote = stockLoteAnterior.subtract(cantidad);
            }
            stockLote.setCantidadDisponible(nuevoStockLote);
            stockLoteRepository.save(stockLote);
        }
        // Registrar movimiento en Kardex
        Kardex kardex = new Kardex();
        kardex.setAlmacen(almacen);
        kardex.setProducto(producto);
        kardex.setCantidad(cantidad);
        kardex.setTipoMovimiento(request.getTipoAjuste().equalsIgnoreCase("POSITIVO") ?
            com.granotec.inventory_api.common.enums.TipoMovimiento.ENTRADA :
            com.granotec.inventory_api.common.enums.TipoMovimiento.SALIDA);
        kardex.setTipoOperacion(com.granotec.inventory_api.common.enums.TypeOperation.OTROS);
        kardex.setStockAnterior(stockAnterior);
        kardex.setStockActual(nuevoStock);
        kardex.setObservacion(request.getMotivo());
        kardex.setFechaMovimiento(LocalDate.now());
        if (stockLote != null) {
            kardex.setLote(stockLote.getLote());
        }
        kardexRepository.save(kardex);
        // Mapear respuesta
        AjusteResponse response = new AjusteResponse();
        response.setIdAjuste(kardex.getId());
        response.setTipoAjuste(request.getTipoAjuste());
        response.setAlmacenId(almacen.getId());
        response.setAlmacenNombre(almacen.getNombre());
        response.setProductoId(producto.getId().longValue());
        response.setProductoNombre(producto.getNombreComercial());
        if (stockLote != null) {
            response.setLoteId(stockLote.getLote().getId());
            response.setCodigoLote(stockLote.getLote().getCodigoLote());
        }
        response.setCantidad(cantidad);
        response.setMotivo(request.getMotivo());
        response.setFechaAjuste(LocalDate.now().toString());
        // Usuario (opcional)
        response.setUsuario(request.getUsuarioId() != null ? request.getUsuarioId().toString() : null);
        return response;
    }

    @Override
    public List<AjusteResponse> listarAjustes() {
        var ajustes = kardexRepository.findByTipoOperacion(com.granotec.inventory_api.common.enums.TypeOperation.OTROS);
        var lista = new java.util.ArrayList<AjusteResponse>();
        for (var kardex : ajustes) {
            AjusteResponse response = new AjusteResponse();
            response.setIdAjuste(kardex.getId());
            response.setTipoAjuste(kardex.getTipoMovimiento() == com.granotec.inventory_api.common.enums.TipoMovimiento.ENTRADA ? "POSITIVO" : "NEGATIVO");
            response.setAlmacenId(kardex.getAlmacen().getId());
            response.setAlmacenNombre(kardex.getAlmacen().getNombre());
            response.setProductoId(kardex.getProducto().getId().longValue());
            response.setProductoNombre(kardex.getProducto().getNombreComercial());
            if (kardex.getLote() != null) {
                response.setLoteId(kardex.getLote().getId());
                response.setCodigoLote(kardex.getLote().getCodigoLote());
            }
            response.setCantidad(kardex.getCantidad());
            response.setMotivo(kardex.getObservacion());
            response.setFechaAjuste(kardex.getFechaMovimiento().toString());
            // Usuario (no disponible en Kardex, solo en request original)
            response.setUsuario(null);
            lista.add(response);
        }
        return lista;
    }
}
