package com.granotec.inventory_api.ov;

import com.granotec.inventory_api.common.exception.BadRequestException;
import com.granotec.inventory_api.common.exception.ResourceNotFoundException;
import com.granotec.inventory_api.ov.dto.OvRequest;
import com.granotec.inventory_api.ov.dto.OvResponse;
import com.granotec.inventory_api.ov.dto.OvStatsResponse;
import com.granotec.inventory_api.customer.CustomerRepository;
import com.granotec.inventory_api.salesperson.SalespersonRepository;
import com.granotec.inventory_api.customer.Customer;
import com.granotec.inventory_api.salesperson.Salesperson;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OvService {

    private final OvRepository repository;
    private final CustomerRepository customerRepository;
    private final SalespersonRepository salespersonRepository;

    public OvResponse create(OvRequest req) {
        Customer c = customerRepository.findById(req.getIdCliente())
                .orElseThrow(() -> new BadRequestException("Cliente no encontrado"));
        Salesperson s = null;
        if (req.getIdVendedor() != null) {
            s = salespersonRepository.findById(req.getIdVendedor())
                    .orElseThrow(() -> new BadRequestException("Vendedor no encontrado"));
        }
        Ov o = new Ov();
        o.setNumeroDocumento(req.getNumeroDocumento());
        o.setTipoDocumento(req.getTipoDocumento());
        o.setCustomer(c);
        o.setSalesperson(s);
        o.setFecha(req.getFecha());
        o.setCurrency(req.getMoneda());
        o.setTotal(req.getTotal());

        o = repository.save(o);
        return toDto(o);
    }

    public OvResponse update(Integer id, OvRequest req) {
        Ov o = repository.findById(id)
                .filter(x -> !Boolean.TRUE.equals(x.getIsDeleted()))
                .orElseThrow(() -> new ResourceNotFoundException("Orden de venta no encontrada"));
        Customer c = customerRepository.findById(req.getIdCliente())
                .orElseThrow(() -> new BadRequestException("Cliente no encontrado"));
        Salesperson s = null;
        if (req.getIdVendedor() != null) {
            s = salespersonRepository.findById(req.getIdVendedor())
                    .orElseThrow(() -> new BadRequestException("Vendedor no encontrado"));
        }
        o.setNumeroDocumento(req.getNumeroDocumento());
        o.setTipoDocumento(req.getTipoDocumento());
        o.setCustomer(c);
        o.setSalesperson(s);
        o.setFecha(req.getFecha());
        o.setCurrency(req.getMoneda());
        o.setTotal(req.getTotal());

        o = repository.save(o);
        return toDto(o);
    }

    public Page<OvResponse> list(Pageable pageable) {
        return repository.findAll(pageable).map(this::toDto);
    }

    public OvResponse getById(Integer id) {
        Ov o = repository.findById(id)
                .filter(x -> !Boolean.TRUE.equals(x.getIsDeleted()))
                .orElseThrow(() -> new ResourceNotFoundException("Orden de venta no encontrada"));
        return toDto(o);
    }

    @Transactional
    public void softDelete(Integer id) {
        Ov o = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden de venta no encontrada"));
        if (Boolean.TRUE.equals(o.getIsDeleted())) {
            throw new BadRequestException("Orden ya eliminada");
        }
        o.softDelete();
        repository.save(o);
    }

    // export csv of ov
    public ByteArrayInputStream exportCsv() {
        List<OvResponse> data = repository.findAll()
                .stream()
                .filter(x -> !Boolean.TRUE.equals(x.getIsDeleted()))
                .map(this::toDto)
                .collect(Collectors.toList());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter pw = new PrintWriter(baos, true, StandardCharsets.UTF_8);
        pw.println("id,numeroDocumento,tipoDocumento,idCliente,idVendedor,fecha,moneda,total");
        DateTimeFormatter df = DateTimeFormatter.ISO_LOCAL_DATE;
        for (OvResponse r : data) {
            pw.printf("%s,%s,%s,%s,%s,%s,%s,%s\n",
                    safe(r.getId()), safe(r.getNumeroDocumento()), safe(r.getTipoDocumento()), safe(r.getIdCliente()), safe(r.getIdVendedor()),
                    r.getFecha() != null ? r.getFecha().format(df) : "", safe(r.getMoneda()), safe(r.getTotal()));
        }
        pw.flush();
        return new ByteArrayInputStream(baos.toByteArray());
    }

    private String safe(Object o) {
        return o == null ? "" : o.toString().replace(",", " ");
    }

    private OvResponse toDto(Ov o) {
        return new OvResponse(
                o.getId(),
                o.getNumeroDocumento(),
                o.getTipoDocumento() != null ? o.getTipoDocumento().name() : null,
                o.getCustomer() != null ? o.getCustomer().getId() : null,
                o.getSalesperson() != null ? o.getSalesperson().getId() : null,
                o.getFecha(),
                o.getCurrency() != null ? o.getCurrency().name() : null,
                o.getTotal()
        );
    }

    public OvStatsResponse getStats(Long customerId, LocalDate from, LocalDate to) {
        if (from == null || to == null) {
            throw new BadRequestException("Los parámetros 'from' y 'to' son obligatorios");
        }
        if (customerId != null) {
            long total = repository.countByCustomerIdAndFechaBetween(customerId, from, to);
            BigDecimal amount = repository.sumTotalByCustomerIdAndFechaBetween(customerId, from, to);
            return new OvStatsResponse(customerId, total, amount);
        } else {
            long total = repository.countByFechaBetween(from, to);
            BigDecimal amount = repository.sumTotalByFechaBetween(from, to);
            return new OvStatsResponse(null, total, amount);
        }
    }

}
