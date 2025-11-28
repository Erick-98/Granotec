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
            result = stockAlmacenRepository.findByProductoIdAndAlmacenId(productoId, almacenId, pageable);
        } else if (productoId != null) {
            result = stockAlmacenRepository.findByProductoId(productoId, pageable);
        } else if (almacenId != null) {
            result = stockAlmacenRepository.findByAlmacenId(almacenId, pageable);
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
            result = stockLoteRepository.findByLoteProductoIdAndAlmacenId(productoId, almacenId, pageable);
        } else if (productoId != null) {
            result = stockLoteRepository.findByLoteProductoId(productoId, pageable);
        } else if (almacenId != null) {
            result = stockLoteRepository.findByAlmacenId(almacenId, pageable);
        } else {
            result = stockLoteRepository.findAll(pageable);
        }

        Page<StockDisponibleResponse> dtoPage = result.map(stockLoteMapper::toDto);
        return ResponseEntity.ok(dtoPage);
    }
}
