package com.granotec.inventory_api.Despacho;


import com.granotec.inventory_api.Despacho.dto.DespachoRequest;
import com.granotec.inventory_api.Movimientos.MovimientoService;
import com.granotec.inventory_api.Movimientos.dto.MovimientoLineRequest;
import com.granotec.inventory_api.Movimientos.dto.MovimientoRequest;
import com.granotec.inventory_api.common.enums.TipoMovimiento;
import com.granotec.inventory_api.common.enums.TypeOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DespachoService {

    private final DespachoRepository despachoRepository;
    private final MovimientoService movimientoService;

    @Transactional
    public Despacho create(DespachoRequest req){
        Despacho d = new Despacho();
        d.setOrdenVenta(req.getOrdenVenta());
        d.setTipoOV(req.getTipoOV());
        d.setCliente(req.getCliente());
        d.setDestino(req.getDestino());
        d.setVendedor(req.getVendedor());
        d.setFechaDespacho(req.getFechaDespacho());
        d.setChoferAsignado(req.getChoferAsignado());
        d.setPlaca(req.getPlaca());
        d.setTransportista(req.getTransportista());
        d.setStatus("PENDING");
        d.setCostoFlete(req.getCostoFlete());

        // convertir detalles a movimientos SALIDA
        MovimientoRequest mr = new MovimientoRequest();
        mr.setFechaDocumento(req.getFechaDespacho());
        // no almacén por defecto; si aplicara, extender req
        mr.setTipoMovimiento(TipoMovimiento.SALIDA);
        mr.setTipoOperacion(TypeOperation.DESPACHO);

        if(req.getDetalles() != null){
            List<MovimientoLineRequest> lines = req.getDetalles().stream().map(l -> new MovimientoLineRequest(l.getProductCode(), l.getLote(), null, null, null, null, null, java.math.BigDecimal.valueOf(l.getKilos()), null, null)).collect(Collectors.toList());
            mr.setDetalles(lines);
        }

        movimientoService.create(mr);

        return despachoRepository.save(d);
    }
}

