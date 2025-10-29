package com.granotec.inventory_api.salesperson;

import com.granotec.inventory_api.common.exception.BadRequestException;
import com.granotec.inventory_api.common.exception.ResourceNotFoundException;
import com.granotec.inventory_api.ov.OvRepository;
import com.granotec.inventory_api.ov.dto.OvResponse;
import com.granotec.inventory_api.salesperson.dto.SalespersonRequest;
import com.granotec.inventory_api.salesperson.dto.SalespersonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SalespersonService {

    private final SalespersonRepository repository;
    private final OvRepository ovRepository;

    public Page<SalespersonResponse> listAll(int page, int size, String q){
        Pageable p = PageRequest.of(page, size);
        Page<Salesperson> result;
        if (q == null || q.isBlank()) result = repository.findAll(p);
        else result = repository.findByNameContainingIgnoreCaseOrNroDocumentoContainingIgnoreCase(q, q, p);
        return result.map(this::toDto);
    }

    public SalespersonResponse getById(Integer id){
        Salesperson s = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Vendedor no encontrado"));
        return toDto(s);
    }

    public SalespersonResponse create(SalespersonRequest req){
        Salesperson s = new Salesperson();
        s.setName(req.getName());
        s.setApellidos(req.getApellidos());
        s.setNroDocumento(req.getNroDocumento());
        s.setTelefono(req.getTelefono());
        s.setEmail(req.getEmail());
        s = repository.save(s);
        return toDto(s);
    }

    public SalespersonResponse update(Integer id, SalespersonRequest req){
        Salesperson s = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Vendedor no encontrado"));
        s.setName(req.getName());
        s.setApellidos(req.getApellidos());
        s.setNroDocumento(req.getNroDocumento());
        s.setTelefono(req.getTelefono());
        s.setEmail(req.getEmail());
        s = repository.save(s);
        return toDto(s);
    }

    public void delete(Integer id){
        Salesperson s = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Vendedor no encontrado"));
        if (s.getOrdenesDeVenta() != null && !s.getOrdenesDeVenta().isEmpty()){
            throw new BadRequestException("No se puede eliminar vendedor con órdenes asociadas");
        }
        s.softDelete();
        repository.save(s);
    }

    public SalespersonResponse toDto(Salesperson s){
        return new SalespersonResponse(s.getId(), s.getName(), s.getApellidos(), s.getNroDocumento(), s.getTelefono(), s.getEmail());
    }

    // Stats endpoints
    public java.util.Map<String, Object> statsForSalesperson(Integer salespersonId){
        repository.findById(salespersonId).orElseThrow(() -> new ResourceNotFoundException("Vendedor no encontrado"));
        var totalOrders = ovRepository.countBySalespersonId(salespersonId);
        var totalAmount = ovRepository.sumTotalBySalespersonId(salespersonId);
        var lastOrder = ovRepository.maxFechaBySalespersonId(salespersonId);
        return java.util.Map.of(
                "totalOrders", totalOrders,
                "totalAmount", totalAmount != null ? totalAmount : BigDecimal.ZERO,
                "lastOrder", lastOrder
        );
    }

    public List<OvResponse> listOrders(Integer salespersonId, int page, int size){
        Page<com.granotec.inventory_api.ov.Ov> p = ovRepository.findBySalespersonId(salespersonId, PageRequest.of(page,size));
        return p.map(o -> new OvResponse(o.getId(), o.getNumeroDocumento(), o.getTipoDocumento() != null ? o.getTipoDocumento().name() : null, o.getCustomer()!=null?o.getCustomer().getId():null, o.getSalesperson()!=null?o.getSalesperson().getId():null, o.getFecha(), o.getCurrency()!=null?o.getCurrency().name():null, o.getTotal())).getContent();
    }
}
