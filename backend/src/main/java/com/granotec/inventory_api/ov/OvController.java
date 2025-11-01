package com.granotec.inventory_api.ov;

import com.granotec.inventory_api.common.dto.ApiResponse;
import com.granotec.inventory_api.ov.dto.OvRequest;
import com.granotec.inventory_api.ov.dto.OvResponse;
import com.granotec.inventory_api.ov.dto.OvStatsResponse;
import com.granotec.inventory_api.ov.details_ov.dto.DetailsOvRequest;
import com.granotec.inventory_api.ov.details_ov.dto.DetailsOvResponse;
import com.granotec.inventory_api.ov.details_ov.DetailsOvService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/ov")
@RequiredArgsConstructor
public class OvController {

    private final OvService service;
    private final DetailsOvService detailsService;

    @PostMapping
    public ResponseEntity<ApiResponse<OvResponse>> create(@Valid @RequestBody OvRequest dto){
        return ResponseEntity.ok(new ApiResponse<>("OV creada", service.create(dto)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<OvResponse>>> list(@PageableDefault(size = 20) Pageable pageable){
        return ResponseEntity.ok(new ApiResponse<>("Listado de OV", service.list(pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OvResponse>> get(@PathVariable Integer id){
        return ResponseEntity.ok(new ApiResponse<>("OV encontrada", service.getById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<OvResponse>> update(@PathVariable Integer id, @Valid @RequestBody OvRequest dto){
        return ResponseEntity.ok(new ApiResponse<>("OV actualizada", service.update(id,dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id){
        service.softDelete(id);
        return ResponseEntity.ok(new ApiResponse<>("OV eliminada", null));
    }

    @GetMapping("/{id}/details")
    public ResponseEntity<ApiResponse<List<DetailsOvResponse>>> getDetailsByOv(@PathVariable Integer id){
        return ResponseEntity.ok(new ApiResponse<>("Detalles de OV", detailsService.listByOvId(id)));
    }

    @PostMapping("/details")
    public ResponseEntity<ApiResponse<DetailsOvResponse>> createDetail(@Valid @RequestBody DetailsOvRequest dto){
        return ResponseEntity.ok(new ApiResponse<>("Detalle OV creado", detailsService.create(dto)));
    }

    @GetMapping("/export")
    public ResponseEntity<InputStreamResource> exportCsv() throws IOException {
        ByteArrayInputStream in = service.exportCsv();
        InputStreamResource resource = new InputStreamResource(in);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=ov.csv");
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @GetMapping("/details/{id}")
    public ResponseEntity<ApiResponse<DetailsOvResponse>> getDetail(@PathVariable Integer id){
        return ResponseEntity.ok(new ApiResponse<>("Detalle encontrado", detailsService.getById(id)));
    }

    @PutMapping("/details/{id}")
    public ResponseEntity<ApiResponse<DetailsOvResponse>> updateDetail(@PathVariable Integer id, @Valid @RequestBody DetailsOvRequest dto){
        return ResponseEntity.ok(new ApiResponse<>("Detalle OV actualizado", detailsService.update(id,dto)));
    }

    @DeleteMapping("/details/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteDetail(@PathVariable Integer id){
        detailsService.softDelete(id);
        return ResponseEntity.ok(new ApiResponse<>("Detalle OV eliminado", null));
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<OvStatsResponse>> stats(@RequestParam(required = false) Long customerId,
                                                              @RequestParam String from,
                                                              @RequestParam String to){
        LocalDate f = LocalDate.parse(from);
        LocalDate t = LocalDate.parse(to);
        return ResponseEntity.ok(new ApiResponse<>("Estadísticas OV", service.getStats(customerId, f, t)));
    }

}
