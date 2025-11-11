package com.granotec.inventory_api.product;

import com.granotec.inventory_api.common.dto.ApiResponse;
import com.granotec.inventory_api.product.dto.ProductRequest;
import com.granotec.inventory_api.product.dto.ProductResponse;
import com.granotec.inventory_api.product.dto.ProductStockDetailsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PreAuthorize("@permissionService.has('product:read')")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> list(@RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "20") int size,
                                                      @RequestParam(required = false) String q){
        return ResponseEntity.ok(new ApiResponse<>("Listado de productos", productService.listAll(page,size,q)));
    }

    @PreAuthorize("@permissionService.has('product:read')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> get(@PathVariable Integer id){
        return ResponseEntity.ok(new ApiResponse<>("Producto encontrado", productService.getById(id)));
    }

    @PreAuthorize("@permissionService.has('product:stock:view')")
    @GetMapping("/{id}/stock")
    public ResponseEntity<ApiResponse<ProductStockDetailsResponse>> stock(@PathVariable Integer id){
        return ResponseEntity.ok(new ApiResponse<>("Stock del producto", productService.getStockDetails(id)));
    }

    @PatchMapping("/{id}/lock")
    @PreAuthorize("@permissionService.has('product:lock')")
    public ResponseEntity<ApiResponse<String>> lock(@PathVariable Integer id, @RequestParam boolean locked, @RequestParam(required = false) String reason){
        productService.setBlock(id, locked, reason);
        return ResponseEntity.ok(new ApiResponse<>("OK", ""));
    }

    @PreAuthorize("@permissionService.has('product:create')")
    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> create(@Valid @RequestBody ProductRequest dto){
        return ResponseEntity.ok(new ApiResponse<>("Producto creado", productService.create(dto)));
    }

    @PreAuthorize("@permissionService.has('product:update')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> update(@PathVariable Integer id, @Valid @RequestBody ProductRequest dto){
        return ResponseEntity.ok(new ApiResponse<>("Producto actualizado", productService.update(id,dto)));
    }

    @PreAuthorize("@permissionService.has('product:delete')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id){
        productService.softDelete(id);
        return ResponseEntity.ok(new ApiResponse<>("Producto eliminado", null));
    }

    @PostMapping("/{id}/stock/adjust")
    @PreAuthorize("@permissionService.has('product:adjust')")
    public ResponseEntity<ApiResponse<String>> adjustStock(@PathVariable Integer id, @RequestBody Map<String, Object> body){
        // body: { "almacenId":1, "lote": "L1", "delta": -5, "notes":"ajuste" }
        Long almacenId = body.get("almacenId") == null ? null : Long.valueOf(body.get("almacenId").toString());
        String lote = body.get("lote") == null ? null : body.get("lote").toString();
        BigDecimal delta = new BigDecimal(body.get("delta").toString());
        String notes = body.get("notes") == null ? null : body.get("notes").toString();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String user = (auth != null && auth.getName() != null) ? auth.getName() : "system";

        productService.adjustStockForProduct(id, almacenId, lote, delta, notes, user);
        return ResponseEntity.ok(new ApiResponse<>("OK",""));
    }

}
