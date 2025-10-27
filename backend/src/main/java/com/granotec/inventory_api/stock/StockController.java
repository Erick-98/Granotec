package com.granotec.inventory_api.stock;

import com.granotec.inventory_api.common.dto.ApiResponse;
import com.granotec.inventory_api.stock.dto.StockRequest;
import com.granotec.inventory_api.stock.dto.StockResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/stock")
@RequiredArgsConstructor
public class StockController {

    private final StockService service;

    @PostMapping
    public ResponseEntity<ApiResponse<StockResponse>> create(@Valid @RequestBody StockRequest req){
        StockResponse resp = service.create(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>("Stock creado correctamente", resp));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<StockResponse>>> list(){
        return ResponseEntity.ok(new ApiResponse<>("Listado stock", service.listAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<StockResponse>> get(@PathVariable Long id){
        return ResponseEntity.ok(new ApiResponse<>("Stock encontrado", service.getById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<StockResponse>> update(@PathVariable Long id, @Valid @RequestBody StockRequest req){
        return ResponseEntity.ok(new ApiResponse<>("Stock actualizado", service.update(id, req)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id){
        service.softDelete(id);
        return ResponseEntity.ok(new ApiResponse<>("Stock eliminado", null));
    }

    @PostMapping("/adjust")
    public ResponseEntity<ApiResponse<StockResponse>> adjust(@RequestParam Long idAlmacen,
                                                             @RequestParam Integer idProducto,
                                                             @RequestParam(required = false) String lote,
                                                             @RequestParam BigDecimal delta){
        StockResponse resp = service.adjustStock(idAlmacen, idProducto, lote, delta);
        return ResponseEntity.ok(new ApiResponse<>("Stock ajustado", resp));
    }
}

