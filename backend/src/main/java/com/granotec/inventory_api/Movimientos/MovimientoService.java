package com.granotec.inventory_api.Movimientos;

import com.granotec.inventory_api.Movimientos.detalle.Movimiento_Detalle;
import com.granotec.inventory_api.Movimientos.dto.MovimientoLineRequest;
import com.granotec.inventory_api.Movimientos.dto.MovimientoLineResponse;
import com.granotec.inventory_api.Movimientos.dto.MovimientoRequest;
import com.granotec.inventory_api.Movimientos.dto.MovimientoResponse;
import com.granotec.inventory_api.common.exception.BadRequestException;
import com.granotec.inventory_api.common.exception.ResourceNotFoundException;
import com.granotec.inventory_api.product.Product;
import com.granotec.inventory_api.product.ProductRepository;
import com.granotec.inventory_api.storage.StorageRepository;
import com.granotec.inventory_api.vendor.VendorRepository;
import com.granotec.inventory_api.customer.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovimientoService {

    private final MovimientoRepository movimientoRepository;
    private final ProductRepository productRepository;
    private final StorageRepository storageRepository;
    private final VendorRepository vendorRepository;
    private final CustomerRepository customerRepository;

    @Transactional
    public MovimientoResponse create(MovimientoRequest request){
        Movimiento m = new Movimiento();
        m.setFechaDocumento(request.getFechaDocumento());
        if(request.getAlmacenId() != null){
            m.setAlmacen(storageRepository.findById(request.getAlmacenId()).orElseThrow(() -> new ResourceNotFoundException("Almacen no encontrado")));
        }
        m.setTipoMovimiento(request.getTipoMovimiento());
        m.setTipoOperacion(request.getTipoOperacion());
        m.setNroFactura(request.getNroFactura());
        if(request.getProveedorId() != null){
            m.setProveedor(vendorRepository.findById(request.getProveedorId()).orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado")));
        }
        if(request.getClienteId() != null){
            m.setDestino_cliente(customerRepository.findById(request.getClienteId()).orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado")));
        }

        BigDecimal totalSoles = BigDecimal.ZERO;
        BigDecimal totalDolares = BigDecimal.ZERO;

        List<Movimiento_Detalle> detalles = new java.util.ArrayList<>();
        if(request.getDetalles() != null){
            for(MovimientoLineRequest d : request.getDetalles()){
                Movimiento_Detalle det = new Movimiento_Detalle();
                Product p = productRepository.findByCode(d.getProductCode()).orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado: " + d.getProductCode()));
                // si es SALIDA validar isLocked
                if(request.getTipoMovimiento() == com.granotec.inventory_api.common.enums.TipoMovimiento.SALIDA && Boolean.TRUE.equals(p.getIsLocked())){
                    throw new BadRequestException("Producto " + p.getCode() + " esta bloqueado");
                }
                det.setProducto(p);
                det.setLote(d.getLote());
                det.setOrdenProduccion(d.getOrdenProduccion());
                det.setFechaIngreso(d.getFechaIngreso());
                det.setFechaProduccion(d.getFechaProduccion());
                det.setFechaVencimiento(d.getFechaVencimiento());
                det.setPresentacion(d.getPresentacion());
                det.setCantidad(d.getCantidad());
                det.setCostoUnitarioSoles(d.getCostoUnitarioSoles());
                det.setCostoUnitarioDolares(d.getCostoUnitarioDolares());
                det.setFamiliaProducto(p.getFamilia() != null ? p.getFamilia().getNombre() : null);
                det.setTipoProducto(p.getTipoProducto());
                det.setMovimiento(m);

                if(d.getCostoUnitarioSoles() != null && d.getCantidad() != null){
                    totalSoles = totalSoles.add(d.getCostoUnitarioSoles().multiply(d.getCantidad()));
                }
                if(d.getCostoUnitarioDolares() != null && d.getCantidad() != null){
                    totalDolares = totalDolares.add(d.getCostoUnitarioDolares().multiply(d.getCantidad()));
                }

                detalles.add(det);
            }
        }

        m.setDetalles(detalles);
        m.setTotalSoles(totalSoles);
        m.setTotalDolares(totalDolares);

        m = movimientoRepository.save(m);
        // detalles se guardan por cascade

        return toDto(m);
    }

    public Page<MovimientoResponse> listAll(int page, int size){
        Pageable pageable = PageRequest.of(page,size);
        Page<Movimiento> p = movimientoRepository.findAll(pageable);
        List<MovimientoResponse> list = p.getContent().stream().map(this::toDto).collect(Collectors.toList());
        return new PageImpl<>(list, pageable, p.getTotalElements());
    }

    public MovimientoResponse getById(Integer id){
        Movimiento m = movimientoRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Movimiento no encontrado"));
        return toDto(m);
    }

    private MovimientoResponse toDto(Movimiento m){
        MovimientoResponse r = new MovimientoResponse();
        r.setId(m.getId());
        r.setFechaDocumento(m.getFechaDocumento());
        if(m.getAlmacen() != null){
            r.setAlmacenId(m.getAlmacen().getId());
            r.setAlmacenNombre(m.getAlmacen().getNombre());
        }
        r.setNroFactura(m.getNroFactura());
        r.setTotalSoles(m.getTotalSoles());
        r.setTotalDolares(m.getTotalDolares());
        List<MovimientoLineResponse> details = m.getDetalles().stream().map(d -> {
            MovimientoLineResponse lr = new MovimientoLineResponse();
            lr.setId(d.getId());
            lr.setProductCode(d.getProducto() != null ? d.getProducto().getCode() : null);
            lr.setNombreComercial(d.getProducto() != null ? d.getProducto().getNombreComercial() : null);
            lr.setLote(d.getLote());
            lr.setOrdenProduccion(d.getOrdenProduccion());
            lr.setFechaIngreso(d.getFechaIngreso());
            lr.setFechaProduccion(d.getFechaProduccion());
            lr.setFechaVencimiento(d.getFechaVencimiento());
            lr.setPresentacion(d.getPresentacion());
            lr.setCantidad(d.getCantidad());
            lr.setCostoUnitarioSoles(d.getCostoUnitarioSoles());
            lr.setCostoUnitarioDolares(d.getCostoUnitarioDolares());
            return lr;
        }).collect(Collectors.toList());
        r.setDetalles(details);
        return r;
    }
}
