package com.granotec.inventory_api.stock;

import com.granotec.inventory_api.stock.dto.StockAdjustRequest;
import com.granotec.inventory_api.stock.dto.StockResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/stock")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    @GetMapping
    public ResponseEntity<List<StockResponse>> listAll(){
        return ResponseEntity.ok(stockService.listAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StockResponse> getById(@PathVariable Long id){
        return ResponseEntity.ok(stockService.getById(id));
    }

    @PostMapping("/adjust")
    @Transactional
    public ResponseEntity<String> adjustStock(@Valid @RequestBody StockAdjustRequest req){
        // obtener usuario autenticado desde el JWT (SecurityContext)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String user = (auth != null && auth.getName() != null) ? auth.getName() : "system";

        // delegar el ajuste y la auditoría al servicio
        stockService.recordAdjustment(req.getIdAlmacen(), req.getIdProducto(), req.getLote(), req.getDelta(), req.getNotes(), user);

        return ResponseEntity.ok("Ajuste registrado");
    }
}
