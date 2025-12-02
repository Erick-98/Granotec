package com.granotec.inventory_api.customer;

import com.granotec.inventory_api.common.enums.CondicionPago;
import com.granotec.inventory_api.common.enums.DocumentType;
import com.granotec.inventory_api.common.exception.BadRequestException;
import com.granotec.inventory_api.common.exception.ResourceNotFoundException;
import com.granotec.inventory_api.customer.dto.CustomerRequest;
import com.granotec.inventory_api.customer.dto.CustomerResponse;
import com.granotec.inventory_api.customer.typeCustomer.TypeCustomer;
import com.granotec.inventory_api.customer.typeCustomer.TypeCustomerRepository;
import com.granotec.inventory_api.location.entity.District;
import com.granotec.inventory_api.location.repository.DistrictRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository cuRepository;
    private final DistrictRepository disRepository;
    private final TypeCustomerRepository tpcRepository;

    @Transactional
    public CustomerResponse create(CustomerRequest dto) {

        validateDocumento(dto.getTipoDocumento(),dto.getNroDocumento());

        if(dto.getEmail() != null && cuRepository.findByEmail(dto.getEmail()).isPresent()){
            throw new BadRequestException("El correo electrónico ya está en uso");
        }
        if(dto.getNroDocumento() != null && cuRepository.findByNroDocumento(dto.getNroDocumento()).isPresent()){
            throw new BadRequestException("El documento ya está en uso");
        }

        District distrito = disRepository.findById(dto.getDistritoId())
                .orElseThrow(() -> new ResourceNotFoundException("El distrito no existe"));

        TypeCustomer tipoCliente = tpcRepository.findById(dto.getTipoClienteId())
                .orElseThrow(() -> new ResourceNotFoundException("El tipo de cliente no existe"));

        Customer customer = mapToEntity(dto, new Customer());
        customer.setDistrito(distrito);
        customer.setTipoCliente(tipoCliente);

        return mapToResponse(cuRepository.save(customer));
    }

    @Transactional
    public CustomerResponse update(Long id, CustomerRequest dto) {
        Customer existing = cuRepository.findById(id)
                .filter(cu -> !Boolean.TRUE.equals(cu.getIsDeleted()))
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));

        validateDocumento(dto.getTipoDocumento(), dto.getNroDocumento());

        if (dto.getEmail() != null && cuRepository.findByEmail(dto.getEmail()).filter(x -> !x.getId().equals(id)).isPresent()) {
            throw new BadRequestException("El correo electrónico ya está en uso");
        }

        if (dto.getNroDocumento() != null && cuRepository.findByNroDocumento(dto.getNroDocumento()).filter(x -> !x.getId().equals(id)).isPresent()) {
            throw new BadRequestException("El documento ya está en uso.");
        }

        District distrito = disRepository.findById(dto.getDistritoId())
                .orElseThrow(() -> new ResourceNotFoundException("El distrito no existe"));

        TypeCustomer tipoCliente = tpcRepository.findById(dto.getTipoClienteId())
                .orElseThrow(() -> new ResourceNotFoundException("El tipo de cliente no existe"));

        Customer customer = mapToEntity(dto, existing);
        customer.setDistrito(distrito);
        customer.setTipoCliente(tipoCliente);

        return mapToResponse(cuRepository.save(customer));
    }

    @Transactional(readOnly = true)
    public List<CustomerResponse> listAll() {
        return cuRepository.findAll()
                .stream()
                .filter(p -> !Boolean.TRUE.equals(p.getIsDeleted()))
                .map(this::mapToResponse)
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

        return cuRepository.findAll(spec, pageable).map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public CustomerResponse getById(Long id) {
        Customer c = cuRepository.findById(id)
                .filter(cu -> !Boolean.TRUE.equals(cu.getIsDeleted()))
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));
        return mapToResponse(c);
    }

    @Transactional
    public void softDelete(Long id) {
        Customer c = cuRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));
        if(Boolean.TRUE.equals(c.getIsDeleted())){
            throw new BadRequestException("El cliente ya ha sido eliminado");
        }
        c.softDelete();
        cuRepository.save(c);
    }

    private void validateDocumento(DocumentType tipo, String documento){
        if(tipo == DocumentType.DNI && (documento == null || documento.length() != 8)){
            throw new BadRequestException("El DNI debe tener 8 caracteres");
        }
        if(tipo == DocumentType.RUC && (documento == null || documento.length() != 11)){
            throw new BadRequestException("El RUC debe tener 11 caracteres");
        }
    }

    private CustomerResponse mapToResponse(Customer c) {
        return CustomerResponse.builder()
                .id(c.getId())
                .nombre(c.getName())
                .apellidos(c.getApellidos())
                .razonSocial(c.getRazonSocial())
                .zona(c.getZona())
                .rubro(c.getRubro())
                .condicionPago(c.getCondicionPago().name())
                .limiteDolares(c.getLimiteDolares())
                .limiteCreditoSoles(c.getLimiteCreditoSoles())
                .notas(c.getNotas())
                .tipoDocumento(c.getTipoDocumento().name())
                .nroDocumento(c.getNroDocumento())
                .direccion(c.getDireccion())
                .telefono(c.getTelefono())
                .email(c.getEmail())
                .distrito(c.getDistrito() != null ? c.getDistrito().getName() : null)
                .provincia(c.getProvincia() != null ? c.getProvincia().getName() : null)
                .departamento(c.getDepartamento() != null ? c.getDepartamento().getName() : null)
                .tipoCliente(c.getTipoCliente() != null ? c.getTipoCliente().getNombre() : null)
                .build();
    }

    private Customer mapToEntity(CustomerRequest dto, Customer customer) {
        customer.setName(dto.getNombre());
        customer.setApellidos(dto.getApellidos());
        customer.setRazonSocial(dto.getRazonSocial());
        customer.setZona(dto.getZona());
        customer.setRubro(dto.getRubro());
        customer.setCondicionPago(CondicionPago.valueOf(dto.getCondicionPago()));
        customer.setLimiteDolares(dto.getLimiteDolares());
        customer.setLimiteCreditoSoles(dto.getLimiteCreditoSoles());
        customer.setNotas(dto.getNotas());
        customer.setTipoDocumento(DocumentType.valueOf(dto.getTipoDocumento().name()));
        customer.setNroDocumento(dto.getNroDocumento());
        customer.setDireccion(dto.getDireccion());
        customer.setTelefono(dto.getTelefono());
        customer.setEmail(dto.getEmail());
        return customer;
    }

}
