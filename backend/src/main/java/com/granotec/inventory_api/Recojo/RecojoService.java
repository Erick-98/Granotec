package com.granotec.inventory_api.Recojo;

import com.granotec.inventory_api.Movimientos.MovimientoService;
import com.granotec.inventory_api.Movimientos.dto.MovimientoLineRequest;
import com.granotec.inventory_api.Movimientos.dto.MovimientoRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class RecojoService {
    private final RecojoRepository recojoRepository;
    private final com.granotec.inventory_api.Movimientos.MovimientoService movimientoService;

    public RecojoService(RecojoRepository recojoRepository, MovimientoService movimientoService) {
        this.recojoRepository = recojoRepository;
        this.movimientoService = movimientoService;
    }

    public Recojo crearRecojo(Recojo r, MovimientoRequest mr) {
        movimientoService.create(mr);
        return recojoRepository.save(r);
    }

    @Transactional
    public Recojo create(Recojo r){
        // transformar detalles a movimiento ENTRADA
        MovimientoRequest mr = new MovimientoRequest();
        mr.setFechaDocumento(r.getFechaRecojo());
        mr.setTipoMovimiento(com.granotec.inventory_api.common.enums.TipoMovimiento.ENTRADA);
        mr.setTipoOperacion(com.granotec.inventory_api.common.enums.TypeOperation.PENDIENTE_RECOJO);

        if(r.getDetalles() != null){
            List<MovimientoLineRequest> lines = r.getDetalles().stream()
                .map(d -> new MovimientoLineRequest(
                    d.getProducto() != null ? d.getProducto().getCode() : null,
                    d.getLote(),
                    null,
                    null,
                    null,
                    null,
                    null,
                    java.math.BigDecimal.valueOf(d.getKilos()),
                    null,
                    null
                ))
                .collect(Collectors.toList());
            mr.setDetalles(lines);
        }

        movimientoService.create(mr);
        return recojoRepository.save(r);
    }
}
