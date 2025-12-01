package com.granotec.inventory_api.inventario.controller;

import com.granotec.inventory_api.inventario.Mapper.StockAlmacenMapper;
import com.granotec.inventory_api.inventario.Mapper.StockLoteMapper;
import com.granotec.inventory_api.inventario.dto.*;
import com.granotec.inventory_api.inventario.service.InventarioService;
import com.granotec.inventory_api.Stock_Almacen.StockAlmacenRepository;
import com.granotec.inventory_api.StockLote.StockLoteRepository;
import com.granotec.inventory_api.product.ProductRepository;
import com.granotec.inventory_api.storage.StorageRepository;
import com.granotec.inventory_api.Stock_Almacen.StockAlmacen;
import com.granotec.inventory_api.StockLote.StockLote;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/inventario")
public class InventarioController {

    private final InventarioService inventarioService;
    private final StockAlmacenRepository stockAlmacenRepository;
    private final StockLoteRepository stockLoteRepository;
    private final StockLoteMapper stockLoteMapper;
    private final StockAlmacenMapper stockAlmacenMapper;

    // ===== ENDPOINTS DE TRANSFERENCIAS =====

    /**
     * Obtener productos disponibles en un almacén específico (con stock > 0)
     */
    @GetMapping("/almacenes/{almacenId}/productos")
    public ResponseEntity<java.util.List<ProductoDisponibleResponse>> obtenerProductosDisponibles(
            @PathVariable Long almacenId) {

        // Obtener todos los stocks del almacén que tengan cantidad > 0
        java.util.List<StockAlmacen> stocks = stockAlmacenRepository
                .findByAlmacenIdAndIsDeletedFalse(almacenId, org.springframework.data.domain.Pageable.unpaged())
                .stream()
                .filter(s -> s.getCantidad().compareTo(java.math.BigDecimal.ZERO) > 0)
                .toList();

        java.util.List<ProductoDisponibleResponse> response = stocks.stream()
                .map(stock -> {
                    ProductoDisponibleResponse dto = new ProductoDisponibleResponse();
                    dto.setProductoId(stock.getProducto().getId().longValue());
                    dto.setProductoNombre(stock.getProducto().getNombreComercial());
                    dto.setProductoCodigo(stock.getProducto().getCode());
                    dto.setCantidadDisponible(stock.getCantidad());
                    return dto;
                })
                .toList();

        return ResponseEntity.ok(response);
    }

    /**
     * Obtener lotes disponibles de un producto en un almacén específico
     */
    @GetMapping("/almacenes/{almacenId}/productos/{productoId}/lotes")
    public ResponseEntity<java.util.List<LoteDisponibleResponse>> obtenerLotesDisponibles(
            @PathVariable Long almacenId,
            @PathVariable Integer productoId) {

        // Obtener lotes con stock disponible
        java.util.List<StockLote> stockLotes = stockLoteRepository
                .findByLoteProductoIdAndAlmacenIdAndIsDeletedFalse(productoId, almacenId)
                .stream()
                .filter(s -> s.getCantidadDisponible().compareTo(java.math.BigDecimal.ZERO) > 0)
                .filter(s -> "DISPONIBLE".equals(s.getLote().getEstado()))
                .toList();

        java.util.List<LoteDisponibleResponse> response = stockLotes.stream()
                .map(stock -> {
                    LoteDisponibleResponse dto = new LoteDisponibleResponse();
                    dto.setLoteId(stock.getLote().getId().longValue());
                    dto.setCodigoLote(stock.getLote().getCodigoLote());
                    dto.setCantidadDisponible(stock.getCantidadDisponible());
                    dto.setFechaProduccion(stock.getLote().getFechaProduccion());
                    dto.setFechaVencimiento(stock.getLote().getFechaVencimiento());
                    dto.setCostoUnitario(stock.getLote().getCostoUnitario());
                    return dto;
                })
                .sorted(java.util.Comparator.comparing(LoteDisponibleResponse::getFechaProduccion))
                .toList();

        return ResponseEntity.ok(response);
    }

    /**
     * Realizar transferencia entre almacenes con lotes específicos
     */
    @PostMapping("/transferencias")
    public ResponseEntity<MovimientoInventarioResponse> transferir(@Valid @RequestBody TransferenciaRequest request) {
        return ResponseEntity.ok(inventarioService.transferir(request));
    }

    @GetMapping("/stock")
    public ResponseEntity<Page<StockAlmacenResponse>> consultarStock(
            @RequestParam(required = false) Integer productoId,
            @RequestParam(required = false) Long almacenId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("producto.id").ascending());
        Page<StockAlmacen> result;

        if (productoId != null && almacenId != null) {
            result = stockAlmacenRepository.findByProductoIdAndAlmacenIdAndIsDeletedFalse(productoId, almacenId, pageable);
        } else if (productoId != null) {
            result = stockAlmacenRepository.findByProductoIdAndIsDeletedFalse(productoId, pageable);
        } else if (almacenId != null) {
            result = stockAlmacenRepository.findByAlmacenIdAndIsDeletedFalse(almacenId, pageable);
        } else {
            result = stockAlmacenRepository.findAll(pageable);
        }

        Page<StockAlmacenResponse> dtopage = result.map(stockAlmacenMapper::toDTO);
        return ResponseEntity.ok(dtopage);
    }

    @GetMapping("/lotes")
    public ResponseEntity<Page<StockDisponibleResponse>> consultarLotes(
            @RequestParam(required = false) Integer productoId,
            @RequestParam(required = false) Long almacenId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("lote.id").ascending());
        Page<StockLote> result;

        if (productoId != null && almacenId != null) {
            result = stockLoteRepository.findByLoteProductoIdAndAlmacenIdAndIsDeletedFalse(productoId, almacenId, pageable);
        } else if (productoId != null) {
            result = stockLoteRepository.findByLoteProductoIdAndIsDeletedFalse(productoId, pageable);
        } else if (almacenId != null) {
            result = stockLoteRepository.findByAlmacenIdAndIsDeletedFalse(almacenId, pageable);
        } else {
            result = stockLoteRepository.findAll(pageable);
        }

        Page<StockDisponibleResponse> dtoPage = result.map(stockLoteMapper::toDto);
        return ResponseEntity.ok(dtoPage);
    }

    // Endpoint de diagnóstico para ver TODOS los registros sin filtrar
    @GetMapping("/lotes/debug")
    public ResponseEntity<?> debugLotes(
            @RequestParam Integer productoId,
            @RequestParam Long almacenId) {

        java.util.List<java.util.Map<String, Object>> debug = new java.util.ArrayList<>();

        // Buscar TODOS los registros sin filtrar isDeleted
        java.util.List<StockLote> todos = stockLoteRepository.findAll().stream()
                .filter(s -> s.getLote().getProducto().getId().equals(productoId)
                        && s.getAlmacen().getId().equals(almacenId))
                .toList();

        for (StockLote stock : todos) {
            java.util.Map<String, Object> info = new java.util.HashMap<>();
            info.put("id", stock.getId());
            info.put("loteId", stock.getLote().getId());
            info.put("loteCodigo", stock.getLote().getCodigoLote());
            info.put("almacenId", stock.getAlmacen().getId());
            info.put("cantidad", stock.getCantidadDisponible());
            info.put("isDeleted", stock.getIsDeleted());
            info.put("createdAt", stock.getCreatedAt());
            info.put("deletedAt", stock.getDeletedAt());
            debug.add(info);
        }

        return ResponseEntity.ok(java.util.Map.of(
                "totalRegistros", debug.size(),
                "registros", debug
        ));
    }
}
