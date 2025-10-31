package com.granotec.inventory_api.dispatch;

import com.granotec.inventory_api.common.dto.ApiResponse;
import com.granotec.inventory_api.dispatch.dto.DispatchRequest;
import com.granotec.inventory_api.dispatch.dto.DispatchStatusRequest;
import com.granotec.inventory_api.dispatch.dto.DispatchResponse;
import com.granotec.inventory_api.dispatch.dto.DispatchStatsResponse;
import com.granotec.inventory_api.dispatch.details_dispatch.dto.DetailsDispatchRequest;
import com.granotec.inventory_api.dispatch.details_dispatch.dto.DetailsDispatchResponse;
import com.granotec.inventory_api.dispatch.details_dispatch.DetailsDispatchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/dispatch")
@RequiredArgsConstructor
public class DispatchController {

    private final DispatchService service;
    private final DetailsDispatchService detailsService;

    @PostMapping
    public ResponseEntity<ApiResponse<DispatchResponse>> create(@Valid @RequestBody DispatchRequest dto){
        return ResponseEntity.ok(new ApiResponse<>("Despacho creado", service.create(dto)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<DispatchResponse>>> list(@PageableDefault(size = 20) Pageable pageable){
        return ResponseEntity.ok(new ApiResponse<>("Listado de despachos", service.list(pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DispatchResponse>> get(@PathVariable Integer id){
        return ResponseEntity.ok(new ApiResponse<>("Despacho encontrado", service.getById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DispatchResponse>> update(@PathVariable Integer id, @Valid @RequestBody DispatchRequest dto){
        return ResponseEntity.ok(new ApiResponse<>("Despacho actualizado", service.update(id,dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id){
        service.softDelete(id);
        return ResponseEntity.ok(new ApiResponse<>("Despacho eliminado", null));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<DispatchResponse>> updateStatus(@PathVariable Integer id, @Valid @RequestBody DispatchStatusRequest dto){
        return ResponseEntity.ok(new ApiResponse<>("Estado actualizado", service.updateStatus(id, dto.getEstado())));
    }

    @GetMapping("/{id}/details")
    public ResponseEntity<ApiResponse<List<DetailsDispatchResponse>>> getDetailsByDispatch(@PathVariable Integer id){
        return ResponseEntity.ok(new ApiResponse<>("Detalles del despacho", service.listDetailsByDispatchId(id)));
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<DispatchStatsResponse>> stats(@RequestParam String from, @RequestParam String to){
        LocalDate f = LocalDate.parse(from);
        LocalDate t = LocalDate.parse(to);
        return ResponseEntity.ok(new ApiResponse<>("Estadísticas de despachos", service.getStats(f,t)));
    }

    @PostMapping("/details")
    public ResponseEntity<ApiResponse<DetailsDispatchResponse>> createDetail(@Valid @RequestBody DetailsDispatchRequest dto){
        return ResponseEntity.ok(new ApiResponse<>("Detalle creado", detailsService.create(dto)));
    }

    @GetMapping("/details")
    public ResponseEntity<ApiResponse<List<DetailsDispatchResponse>>> listDetails(){
        return ResponseEntity.ok(new ApiResponse<>("Listado de detalles de despacho", detailsService.listAll()));
    }

    @GetMapping("/details/{id}")
    public ResponseEntity<ApiResponse<DetailsDispatchResponse>> getDetail(@PathVariable Integer id){
        return ResponseEntity.ok(new ApiResponse<>("Detalle encontrado", detailsService.getById(id)));
    }

    @PutMapping("/details/{id}")
    public ResponseEntity<ApiResponse<DetailsDispatchResponse>> updateDetail(@PathVariable Integer id, @Valid @RequestBody DetailsDispatchRequest dto){
        return ResponseEntity.ok(new ApiResponse<>("Detalle actualizado", detailsService.update(id,dto)));
    }

    @DeleteMapping("/details/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteDetail(@PathVariable Integer id){
        detailsService.softDelete(id);
        return ResponseEntity.ok(new ApiResponse<>("Detalle eliminado", null));
    }

}
