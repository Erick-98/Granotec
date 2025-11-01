package com.granotec.inventory_api.product;

import com.granotec.inventory_api.product.dto.ProductResponse;
import com.granotec.inventory_api.product.dto.ProductStockDetailsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping
    public ResponseEntity<Page<ProductResponse>> list(@RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "20") int size,
                                                      @RequestParam(required = false) String q){
        return ResponseEntity.ok(productService.listAll(page,size,q));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> get(@PathVariable Integer id){
        return ResponseEntity.ok(productService.getById(id));
    }

    @GetMapping("/{id}/stock")
    public ResponseEntity<ProductStockDetailsResponse> stock(@PathVariable Integer id){
        return ResponseEntity.ok(productService.getStockDetails(id));
    }

    @PatchMapping("/{id}/lock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> lock(@PathVariable Integer id, @RequestParam boolean locked, @RequestParam(required = false) String reason){
        productService.setLock(id, locked, reason);
        return ResponseEntity.ok("OK");
    }

    @PostMapping("/{id}/stock/adjust")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> adjustStock(@PathVariable Integer id, @RequestBody Map<String, Object> body){
        // body: { "almacenId":1, "lote": "L1", "delta": -5, "notes":"ajuste" }
        Long almacenId = body.get("almacenId") == null ? null : Long.valueOf(body.get("almacenId").toString());
        String lote = body.get("lote") == null ? null : body.get("lote").toString();
        BigDecimal delta = new BigDecimal(body.get("delta").toString());
        String notes = body.get("notes") == null ? null : body.get("notes").toString();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String user = (auth != null && auth.getName() != null) ? auth.getName() : "system";

        productService.adjustStockForProduct(id, almacenId, lote, delta, notes, user);
        return ResponseEntity.ok("OK");
    }
}
