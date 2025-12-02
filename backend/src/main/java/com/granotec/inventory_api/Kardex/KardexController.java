package com.granotec.inventory_api.Kardex;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/kardex")
public class KardexController {

    private final KardexRepository kardexRepository;
    private final KardexService kardexService;

    @GetMapping("/producto/{productoId}")
    public ResponseEntity<?> movimientosPorProducto(@PathVariable Integer productoId,
                                                    @RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("fechaMovimiento").descending());
        Page<Kardex> result = kardexRepository.findByProductoId(productoId, pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/lote/{loteId}")
    public ResponseEntity<?> movimientosPorLote(@PathVariable Integer loteId,
                                                @RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("fechaMovimiento").descending());
        Page<Kardex> result = kardexRepository.findByLoteId(loteId, pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/almacen/{almacenId}")
    public ResponseEntity<?> movimientosPorAlmacen(@PathVariable Integer almacenId,
                                                   @RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("fechaMovimiento").descending());
        Page<Kardex> result = kardexRepository.findByAlmacenId(almacenId, pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/search")
    public Page<KardexResponse> buscarKardex(
            @RequestParam(required = false) Integer productoId,
            @RequestParam(required = false) Long almacenId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde ,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("fechaMovimiento").descending());
        return kardexService.searchKardex(productoId, almacenId, desde, hasta, pageable);
    }


}

