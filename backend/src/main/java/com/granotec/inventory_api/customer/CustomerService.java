package com.granotec.inventory_api.customer;

import com.granotec.inventory_api.common.enums.DocumentType;
import com.granotec.inventory_api.common.exception.BadRequestException;
import com.granotec.inventory_api.common.exception.ResourceNotFoundException;
import com.granotec.inventory_api.customer.dto.CustomerRequest;
import com.granotec.inventory_api.customer.dto.CustomerResponse;
import com.granotec.inventory_api.customer.dto.CustomerStatsResponse;
import com.granotec.inventory_api.ov.Ov;
import com.granotec.inventory_api.ov.OvRepository;
import com.granotec.inventory_api.ov.dto.OvResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository repository;
    private final OvRepository ovRepository;

    public CustomerResponse create(CustomerRequest request){
        validateDocumento(request.getTipoDocumento(),request.getDocumento());
        if(request.getEmail() != null && repository.findByEmail(request.getEmail()).isPresent()){
            throw new BadRequestException("El correo electrónico ya está en uso");
        }
        if(request.getDocumento() != null && repository.findByNroDocumento(request.getDocumento()).isPresent()){
            throw new BadRequestException("El documento ya está en uso");
        }

        Customer c = new Customer();
        c.setName(request.getNombre());
        c.setApellidos(request.getApellidos());
        c.setRazonSocial(request.getRazonSocial());
        c.setTipoDocumento(request.getTipoDocumento());
        c.setNroDocumento(request.getDocumento());
        c.setDireccion(request.getDireccion());
        c.setTelefono(request.getTelefono());
        c.setEmail(request.getEmail());

        c = repository.save(c);
        return toDto(c);
    }

    public CustomerResponse update(Long id, CustomerRequest request) {
        Customer c = repository.findById(id)
                .filter(pr -> !Boolean.TRUE.equals(pr.getIsDeleted()))
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));

        validateDocumento(request.getTipoDocumento(), request.getDocumento());

        if (request.getEmail() != null && repository.findByEmail(request.getEmail()).filter(x -> !x.getId().equals(id)).isPresent()) {
            throw new BadRequestException("El correo electrónico ya está en uso");
        }
        if (request.getDocumento() != null && repository.findByNroDocumento(request.getDocumento()).filter(x -> !x.getId().equals(id)).isPresent()) {
            throw new BadRequestException("El documento ya está en uso.");
        }

        // Map fields manually to avoid overwriting id and relations
        c.setName(request.getNombre());
        c.setApellidos(request.getApellidos());
        c.setRazonSocial(request.getRazonSocial());
        c.setTipoDocumento(request.getTipoDocumento());
        c.setNroDocumento(request.getDocumento());
        c.setDireccion(request.getDireccion());
        c.setTelefono(request.getTelefono());
        c.setEmail(request.getEmail());
        c = repository.save(c);
        return toDto(c);
    }

    public List<CustomerResponse> listAll() {
        return repository.findAll()
                .stream()
                .filter(p -> !Boolean.TRUE.equals(p.getIsDeleted()))
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public Page<CustomerResponse> list(Pageable pageable, String nombre, String documento, String email) {
        Specification<Customer> spec = (root, query, cb) -> cb.isFalse(root.get("isDeleted"));

        if (nombre != null && !nombre.isBlank()) {
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("name")), "%" + nombre.toLowerCase() + "%"));
        }
        if (documento != null && !documento.isBlank()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("nroDocumento"), documento));
        }
        if (email != null && !email.isBlank()) {
            spec = spec.and((root, query, cb) -> cb.equal(cb.lower(root.get("email")), email.toLowerCase()));
        }

        return repository.findAll(spec, pageable).map(this::toDto);
    }

    public Page<OvResponse> getCustomerOvs(Long customerId, Pageable pageable) {
        // validate customer exists and not deleted
        Customer c = repository.findById(customerId)
                .filter(pr -> !Boolean.TRUE.equals(pr.getIsDeleted()))
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));

        return ovRepository.findByCustomerId(customerId, pageable)
                .map(this::toOvDto);
    }

    public ByteArrayInputStream exportCsv(String format) {
        // For simplicity export all customers to CSV; format ignored except csv
        List<CustomerResponse> data = listAll();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter pw = new PrintWriter(baos, true, StandardCharsets.UTF_8);

        // Header
        pw.println("id,nombre,apellidos,tipoDocumento,documento,direccion,telefono,email");
        for (CustomerResponse r : data) {
            pw.printf("%d,%s,%s,%s,%s,%s,%s,%s\n",
                    r.getId(),
                    safe(r.getNombre()),
                    safe(r.getApellidos()),
                    safe(r.getTipoDocumento()),
                    safe(r.getDocumento()),
                    safe(r.getDireccion()),
                    safe(r.getTelefono()),
                    safe(r.getEmail())
            );
        }
        pw.flush();
        return new ByteArrayInputStream(baos.toByteArray());
    }

    public CustomerStatsResponse getCustomerStats(Long customerId) {
        Customer c = repository.findById(customerId)
                .filter(pr -> !Boolean.TRUE.equals(pr.getIsDeleted()))
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));

        // Compute stats: total orders, total amount, last order date using repository aggregates
        long totalOrders = ovRepository.countByCustomerId(customerId);
        BigDecimal totalAmount = ovRepository.sumTotalByCustomerId(customerId);
        java.time.LocalDate lastOrderDate = ovRepository.maxFechaByCustomerId(customerId);

        return new CustomerStatsResponse(customerId, totalOrders, totalAmount, lastOrderDate);
    }

    public CustomerResponse getById(Long id) {
        Customer c = repository.findById(id)
                .filter(pr -> !Boolean.TRUE.equals(pr.getIsDeleted()))
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));
        return toDto(c);
    }

    public void softDelete(Long id) {
        Customer c = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));
        if (Boolean.TRUE.equals(c.getIsDeleted())) {
            throw new BadRequestException("El cliente ya ha sido eliminado");
        }
        c.softDelete();
        repository.save(c);
    }

    private void validateDocumento(DocumentType tipo, String documento){
        if(tipo == DocumentType.DNI && (documento == null || documento.length() != 8)){
            throw new BadRequestException("El DNI debe tener 8 caracteres");
        }
        if(tipo == DocumentType.RUC && (documento == null || documento.length() != 11)){
            throw new BadRequestException("El RUC debe tener 11 caracteres");
        }
    }

    private String safe(String v) {
        return v == null ? "" : v.replaceAll(",", " ");
    }

    private CustomerResponse toDto(Customer c) {
        return new CustomerResponse(
                c.getId(),
                c.getName(),
                c.getApellidos(),
                c.getRazonSocial(),
                c.getTipoDocumento() != null ? c.getTipoDocumento().name() : null,
                c.getNroDocumento(),
                c.getDireccion(),
                c.getTelefono(),
                c.getEmail()
        );
    }

    private OvResponse toOvDto(Ov o) {
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

}
